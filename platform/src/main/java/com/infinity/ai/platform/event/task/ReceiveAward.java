package com.infinity.ai.platform.event.task;

import com.infinity.ai.platform.event.ActionService;
import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.domain.model.PlayerTaskReceiveEntity;
import com.infinity.ai.platform.dropsystem.DropFactory;
import com.infinity.ai.platform.event.ActionParams;
import com.infinity.ai.platform.event.ActionTypeEnum;
import com.infinity.ai.platform.event.task.enums.TaskStatusEnum;
import com.infinity.ai.platform.event.task.enums.UnlockTypeEnum;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.PlayerTaskDataManager;
import com.infinity.ai.platform.manager.RepositoryUtils;
import com.infinity.common.base.common.Response;
import com.infinity.common.config.data.DropsCfg;
import com.infinity.common.config.data.GlobalConfig;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.config.manager.TaskDataManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.db.util.Pair;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ReceiveAward {
    //private final PlayerTaskRepository repository;

    public ReceiveAward() {
        //repository = SpringContextHolder.getBean(PlayerTaskRepository.class);
    }

    /**
     * 根据任务ID领取奖励
     *
     * @param playerId 玩家ID
     * @param taskId   任务ID
     * @return
     */
    public Response<ReceiveResponse> receive(long playerId, int taskId) {
        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerId);
        PlayerTaskEntity entity = repository.findByPlayerIdTaskId(playerId, taskId);
        if (entity == null) {
            return Response.createError(ErrorCode.kDrawTaskNullMessage, ErrorCode.kDrawTaskNullError);
        }

        if (entity.getStatus() != TaskStatusEnum.PENDING.getCode()) {
            return Response.createError(ErrorCode.kDrawTaskStatusMessage, ErrorCode.kDrawTaskStatusError);
        }

        return receive(playerId, entity);
    }

    /**
     * 领取奖励
     *
     * @return
     */
    public Response<ReceiveResponse> receive(long playerId, PlayerTaskEntity entity) {
        if (entity == null) {
            return Response.createError(ErrorCode.kDrawTaskNullMessage, ErrorCode.kDrawTaskNullError);
        }

        if (entity.getStatus() != TaskStatusEnum.PENDING.getCode()) {
            return Response.createError(ErrorCode.kDrawTaskStatusMessage, ErrorCode.kDrawTaskStatusError);
        }

        List<PlayerTaskEntity> taskList = Arrays.asList(entity);
        return executeReceive(playerId, taskList);
    }

    /**
     * 一键领取奖励
     *
     * @param playerId 玩家ID
     * @param type     任务类型:1:日常任务,2:成就任务,3:竞技任务
     * @param subType  任务子类型:日常任务（1主线、2日常、3周常、4支线),成就任务(1战斗、2伙伴、3装备进阶、4宠物、5竞技场、6工会),竞技(1.日常、2.周常)
     * @return
     */
    public Response<ReceiveResponse> oneKeyReceive(long playerId, int type, int subType) {
        //查询所有可领取任务信息
        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerId);
        if (type == 1 && subType == 1) {
            TaskCfg taskCfg = null;
            List<TaskCfg> cfgList = GameConfigManager.getInstance().getTaskDataManager().getConfigWithPositionMinPosition(type, 7);
            for (TaskCfg cfg : cfgList) {
                PlayerTaskEntity taskEntity = repository.findByPlayerIdTaskId(playerId, cfg.getId());
                if (taskEntity == null || (taskEntity.getUnlock() == 1 && taskEntity.getStatus() == 1)) {
                    taskCfg = cfg;
                    break;
                }
            }

            if (taskCfg == null) {
                return Response.createError(ErrorCode.kDrawTaskNotExistErrorMessage, ErrorCode.kDrawTaskNotExistError);
            }

            //todo modify
            List<PlayerTaskEntity> taskList = null;//repository.findByPlayerIdTaskIds(playerId, taskCfg.getValue2());
            List<PlayerTaskEntity> collect = filterTaskList(taskList);
            return executeReceive(playerId, collect);
        } else {
            List<PlayerTaskEntity> taskList = repository.findByPlayerIdTypeSubTypeStatus(playerId, type, subType);
            return executeReceive(playerId, taskList);
        }
    }

    private List<PlayerTaskEntity> filterTaskList(List<PlayerTaskEntity> taskList) {
        List<PlayerTaskEntity> results = new ArrayList<>();
        //按ID排序
        List<PlayerTaskEntity> sortList = taskList.stream().sorted(Comparator.comparing(PlayerTaskEntity::getTaskId)).collect(Collectors.toList());
        Map<Integer, PlayerTaskEntity> map = sortList.stream().collect(Collectors.toMap(PlayerTaskEntity::getTaskId, e -> e));

        TaskDataManager manager = GameConfigManager.getInstance().getTaskDataManager();
        for (PlayerTaskEntity entity : sortList) {
            TaskCfg config = manager.getConfigWithID((int) entity.getTaskId());
            if (config == null) {
                continue;
            }

            if (config.getUnlock() == UnlockTypeEnum.FINISH.getCode()) {
                PlayerTaskEntity parentEntity = map.get((long) config.getUnlockvalue());
                if (!isFinish(parentEntity) || !isFinish(entity)) {
                    break;
                }

                if (isAward(entity)) {
                    results.add(entity);
                }

                continue;
            }

            if (isFinish(entity) && isAward(entity)) {
                results.add(entity);
            }
        }
        return results;
    }

    private boolean isFinish(PlayerTaskEntity entity) {
        //状态：0:初始化,1:任务进行中,2:任务完成,3:领取完成
        //解锁:0:未解锁,1:已解锁
        return entity == null || ((entity.getStatus() == 2 || entity.getStatus() == 3) && entity.getUnlock() == 1);
    }

    private boolean isAward(PlayerTaskEntity entity) {
        return entity != null && entity.getStatus() == 2;
    }

    private Response<ReceiveResponse> buildResponse(List<PlayerTaskEntity> taskList, List<Goods> goodsList, Set<Integer> catIds) {
        List<Integer> taskIdList = taskList.stream().map(PlayerTaskEntity::getTaskId).distinct().collect(Collectors.toList());
        return Response.createSuccess(new ReceiveResponse(goodsList, taskIdList, catIds));
    }

    /**
     * 领取奖励
     *
     * @param playerId 玩家ID
     * @return
     */
    private Response<ReceiveResponse> executeReceive(long playerId, List<PlayerTaskEntity> taskList) {
        //查询所有可领取任务信息
        if (ObjectUtils.isEmpty(taskList)) {
            return Response.createError(ErrorCode.kDrawTaskNotExistErrorMessage, ErrorCode.kDrawTaskNotExistError);
        }

        List<PlayerTaskReceiveEntity> receiveList = buildReceiveRecord(taskList);
        /*if (ObjectUtils.isEmpty(receiveList)) {
            return Response.createError(ErrorCode.kDrawTaskNotExistErrorMessage, ErrorCode.kDrawTaskNotExistError);
        }*/

        //组装奖励返回结果
        List<Goods> goodsList = getResult(playerId, getReceiveMap(receiveList));
        Set<Integer> catIds = new HashSet<>();
        //奖励加入玩家账户
        //saveGoodsList(goodsList);
        //GoodsManager.saveGoodsList(playerId, goodsList, GoodsSource.TASK_ACHIEVEMENT);
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        player.bag.addGoods(goodsList, true, GoodsSource.TASK_ACHIEVEMENT);

        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerId);
        //增加领取记录
        if (!ObjectUtils.isEmpty(receiveList)) {
            repository.batchSaveReceive(receiveList);
        }

        //所有的ID
        List<Long> idList = taskList.stream().map(PlayerTaskEntity::getId).distinct().collect(Collectors.toList());
        //更新任务状态
        repository.updateStatusByIds(idList);
        //日活任务
        //doActiveTask(playerId, goodsList);
        //执行根据完成的任务ID做任务
        doNextTask(playerId, taskList);
        return buildResponse(taskList, goodsList, catIds);
    }

    /***
     * 日常活跃奖励任务
     * @param playerId 用户ID
     * @param goodsList 活跃量
     */
    private void doActiveTask(long playerId, List<Goods> goodsList) {
        int activeCount = goodsList.stream().filter(dbGoods -> dbGoods.getGoodsId() == GlobalConfig.kActivityId).mapToInt(Goods::getCount).sum();
        if (activeCount > 0) {
            //ActionService.push(new ActionParams(playerId, ActionTypeEnum.PLAYER_ACTIVE, activeCount));
        }
    }

    //根据完成的任务ID做任务
    protected void doNextTask(long playerId, List<PlayerTaskEntity> taskList) {
        taskList.forEach(entity -> {
            //解锁任务
            ActionService.push(new ActionParams(playerId, ActionTypeEnum.TASK_FINISH, entity.getTaskId()));
        });
    }

    private List<PlayerTaskReceiveEntity> buildReceiveRecord(List<PlayerTaskEntity> taskList) {
        List<PlayerTaskReceiveEntity> entityList = new ArrayList<>();
        TaskDataManager taskDataManager = GameConfigManager.getInstance().getTaskDataManager();
        ItemBaseDataManager itemBaseDataManager = GameConfigManager.getInstance().getItemBaseDataManager();
        taskList.forEach(task -> {
            TaskCfg taskConfig = taskDataManager.getConfigWithID((int) task.getTaskId());
            if (taskConfig == null) {
                return;
            }

            List<Goods> goodsList = DropFactory.getGoodsByDrops(taskConfig.getTaskReward());
            //List<List<Integer>> itemsId = getItemsId(taskConfig);
            goodsList.forEach(goods -> {
                int goodsId = goods.getGoodsId();
                ItemCfg cfg = itemBaseDataManager.getItemConfigWithID(goodsId);
                if (!ObjectUtils.isEmpty(cfg)) {
                    PlayerTaskReceiveEntity re = new PlayerTaskReceiveEntity();
                    re.setTaskId(task.getTaskId());
                    //re.setType(handler.getType());
                    //re.setSubType(handler.getSubType());
                    //re.setRefurbish(handler.getRefurbish());
                    //re.setTaskType(handler.getTaskType());
                    re.setGoodsId(goodsId);
                    re.setGoodsType(cfg.getKind());
                    re.setCount(goods.getCount());
                    entityList.add(re);
                }
            });
        });
        return entityList;
    }

    private Map<Integer, List<PlayerTaskReceiveEntity>> getReceiveMap(List<PlayerTaskReceiveEntity> receiveList) {
        return receiveList.stream().collect(Collectors.groupingBy(PlayerTaskReceiveEntity::getGoodsId));
    }

    private List<Goods> getResult(Long playerId, Map<Integer, List<PlayerTaskReceiveEntity>> receiveMap) {
        List<Goods> goodsList = new ArrayList<>();
        for (Map.Entry<Integer, List<PlayerTaskReceiveEntity>> data : receiveMap.entrySet()) {
            Goods goods = new Goods();
            goods.setGoodsId(data.getKey());
            data.getValue().forEach(d -> {
                goods.setGoodsType(d.getGoodsType());
                goods.setCount((goods.getCount() == null ? 0 : goods.getCount()) + d.getCount());
            });
            goodsList.add(goods);
        }
        return goodsList;
    }

    //todo 配置格式：道具id，数量min，数量max
    private List<List<Integer>> getItemsId(TaskCfg taskConfig) {
        int drop = taskConfig.getTaskReward();
        DropsCfg dropsCfg = GameConfigManager.getInstance().getDropBaseData().getDropsConfigWithID(drop);
        if (dropsCfg != null) {
            //todo getItem_weight
            return Collections.EMPTY_LIST;//dropsCfg.getItem_weight();
        }

        return Collections.emptyList();
    }
}
