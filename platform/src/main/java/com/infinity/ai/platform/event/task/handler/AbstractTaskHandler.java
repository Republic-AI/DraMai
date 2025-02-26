package com.infinity.ai.platform.event.task.handler;

import com.infinity.ai.platform.event.task.*;
import com.infinity.ai.platform.event.task.enums.TaskTypeEnum;
import com.infinity.ai.platform.event.task.enums.UnlockStateEnum;
import com.infinity.ai.platform.manager.PlayerTaskDataManager;
import com.infinity.ai.platform.manager.PlayerTaskManager;
import com.infinity.ai.platform.manager.RepositoryUtils;
import com.infinity.ai.platform.task.task.TaskChangeNotifyTask;
import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.platform.event.task.enums.TaskStatusEnum;
import com.infinity.ai.platform.event.task.enums.UnlockTypeEnum;
import com.infinity.common.base.common.Response;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractTaskHandler extends ReceiveAward implements TaskHandler {
    public AbstractTaskHandler() {
        super();
    }

    @Override
    public boolean execute(PlayerTask playerTask) {
        if (!playerTask.isGm() && ObjectUtils.isEmpty(playerTask.getTaskPrams())) {
            return false;
        }

        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerTask.getPlayerId());
        PlayerTaskEntity entity = repository.findByPlayerIdTaskId(playerTask.getPlayerId(), playerTask.getTaskConfig().getId());
        if (entity == null) {
            entity = playerTask.newEntity();
            repository.save(entity);
        }

        //保存修改前数据
        entity.saveOldValue();

        //任务已做完直接返回
        if (taskIsFinish(entity)) {
            log.debug("handler is finish,playerId={},taskId={}", playerTask.getPlayerId(), entity.getTaskId());
            if (UnlockStateEnum.isNotUnlocked(entity.getUnlock())) {
                entity.setUnlock(playerTask.isUnlock() ? UnlockStateEnum.UNLOCKED.getCode() : UnlockStateEnum.NOT_UNLOCKED.getCode());
                repository.updateValueAndStatus(entity);
                playerTaskChangeNotify(playerTask.getPlayerId(), entity);
            }
            //增加做完记录
            //PlayerTaskManager.getInstance().addValue(playerTask.getPlayerId(), playerTask.getTaskConfig().getId());
            return false;
        }

        boolean result = isMock(playerTask) ? doMock(entity, playerTask) : doTask(entity, playerTask);
        //状态：1:待领取, 2:任务进行中, 3:初始化, 4:领取完成
        entity.setStatus(result ? TaskStatusEnum.PENDING.getCode() : TaskStatusEnum.INPROGRESS.getCode());
        if (UnlockStateEnum.isNotUnlocked(entity.getUnlock())) {
            entity.setUnlock(playerTask.isUnlock() ? UnlockStateEnum.UNLOCKED.getCode() : UnlockStateEnum.NOT_UNLOCKED.getCode());
        }

        repository.updateValueAndStatus(entity);
        playerTaskChangeNotify(playerTask.getPlayerId(), entity);
        notifyNext(playerTask, entity);
        return result;
    }

    private boolean doMock(PlayerTaskEntity entity, PlayerTask playerTask) {
        entity.setValue1(playerTask.getTaskConfig().getValue1());
        return true;
    }

    private boolean isMock(PlayerTask playerTask) {
        return (playerTask.isGm() && (playerTask.getTaskPrams() == null || playerTask.getTaskPrams().length == 0));
    }

    /***
     * 任务做完通知端上
     * @param entity
     */
    protected void playerTaskChangeNotify(long playerId, PlayerTaskEntity entity) {
        if (entity == null || !entity.isChange()) {
            return;
        }

        if (entity.getUnlock() != null && entity.getUnlock() == 1) {
            List<TaskChangeNotifyTask.NotifyData> dataList = new ArrayList<>();
            TaskChangeNotifyTask.NotifyData data = new TaskChangeNotifyTask.NotifyData();
            data.setTaskId((int) entity.getTaskId());
            data.setStatus(entity.getStatus());
            data.setValue((ObjectUtils.isEmpty(entity.getValue1()) ? 0 : entity.getValue1()));
            dataList.add(data);

            PlayerTaskManager.getInstance().playerTaskChangeNotify(playerId, dataList);
        }
    }

    private void notifyNext(PlayerTask playerTask, PlayerTaskEntity entity) {
        if (entity == null || !entity.isChange() || entity.getStatus() != TaskStatusEnum.PENDING.getCode()) {
            return;
        }

        long playerId = playerTask.getPlayerId();
        TaskCfg cfg = playerTask.getTaskConfig();
        //任务类型:1:日常任务,2:成就任务,3:竞技任务
        //任务子类型:日常任务（1主线、2日常、3周常、4支线、5日常活跃、6周常活跃 7主线章节),
        if (cfg.getPosition() == 1 && cfg.getMinPosition() == 1) {
            Threads.runAsync(ThreadConst.QUEUE_LOGIC, playerId, "notifyNext#" + entity.getTaskId(), () -> {
                //按完成解锁任务
                int unlockValue = (int) entity.getTaskId();
                TaskCfg taskConfig = new TaskCfg();
                taskConfig.setUnlock(UnlockTypeEnum.FINISH.getCode());
                taskConfig.setUnlockvalue(unlockValue);
                PlayerTaskContext.getUnlock(UnlockTypeEnum.FINISH.getCode()).unlock(playerId, unlockValue, taskConfig);
            });
        }
    }

    /***
     * 奖励领取
     * @param playerTask 任务
     * @return true：领取成功，false:领取失败
     */
    @Override
    public Response<ReceiveResponse> receive(PlayerTask playerTask) {
        //PlayerTaskRepository repository = SpringContextHolder.getBean(PlayerTaskRepository.class);
        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerTask.getPlayerId());
        PlayerTaskEntity entity = repository.findByPlayerIdTaskId(playerTask.getPlayerId(), playerTask.getTaskConfig().getId());
        if (entity == null) {
            log.debug("handler is not exists,playerId={},taskId={}", playerTask.getPlayerId(), entity.getTaskId());
            return Response.createError(ErrorCode.kDrawTaskNullMessage, ErrorCode.kDrawTaskNullError);
        }

        //任务是否待领取
        if (!taskIsPending(entity)) {
            log.debug("handler is finish,playerId={},taskId={}", playerTask.getPlayerId(), entity.getTaskId());
            return Response.createError(ErrorCode.kDrawTaskStatusMessage, ErrorCode.kDrawTaskStatusError);
        }

        if (UnlockStateEnum.isNotUnlocked(entity.getUnlock())) {
            log.debug("handler is not unlocked,playerId={},taskId={}", playerTask.getPlayerId(), entity.getTaskId());
            return Response.createError(ErrorCode.kDrawTaskNotUnlockMessage, ErrorCode.kDrawTaskNotUnlockError);
        }

        //领取
        boolean result = doReceive(entity, playerTask);
        if (result) {
            return this.receive(playerTask.getPlayerId(), entity);
        }

        return Response.createError();
    }

    //获取奖励列表
    protected List<Goods> getGoodsList(Long playerId, List<List<Integer>> itemIdList) {
        List<Goods> goodsList = new ArrayList<>();
        ItemBaseDataManager itemBaseDataManager = GameConfigManager.getInstance().getItemBaseDataManager();
        itemIdList.forEach(ids -> {
            Integer goodsId = ids.get(0);
            ItemCfg cfg = itemBaseDataManager.getItemConfigWithID(goodsId);
            if (cfg != null) {
                Goods goods = new Goods();
                goods.setGoodsId(goodsId);
                goods.setGoodsType(cfg.getKind());
                goods.setCount(Common.RandomRangeInt(ids.get(1), ids.get(2)));
                goodsList.add(goods);
            }
        });
        return goodsList;
    }

    protected int getValue1(PlayerTaskEntity entity) {
        try {
            return (ObjectUtils.isEmpty(entity.getValue1())) ? 0 : entity.getValue1();
        } catch (Exception e) {
            return 0;
        }
    }

    protected String getValue2(PlayerTaskEntity entity) {
        try {
            return entity.getValue2();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getValue3(PlayerTaskEntity entity) {
        try {
            return entity.getValue3();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean taskIsFinish(PlayerTaskEntity entity) {
        return entity.getStatus() == TaskStatusEnum.PENDING.getCode()
                || entity.getStatus() == TaskStatusEnum.FINISH.getCode();
        //|| UnlockStateEnum.isUnlocked(entity.getUnlock());
    }

    protected boolean taskIsPending(PlayerTaskEntity entity) {
        return entity.getStatus() == TaskStatusEnum.PENDING.getCode() && UnlockStateEnum.isUnlocked(entity.getUnlock());
    }

    protected abstract boolean doTask(PlayerTaskEntity entity, PlayerTask playerTask);

    protected abstract boolean doReceive(PlayerTaskEntity entity, PlayerTask playerTask);

    public abstract TaskTypeEnum getTaskType();
}
