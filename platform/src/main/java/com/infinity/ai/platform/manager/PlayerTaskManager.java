package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.event.task.ReceiveAward;
import com.infinity.ai.platform.task.task.TaskChangeNotifyTask;
import com.infinity.ai.platform.event.task.PlayerTask;
import com.infinity.ai.platform.event.task.ReceiveResponse;
import com.infinity.ai.platform.event.task.enums.TaskTypeEnum;
import com.infinity.common.base.common.Response;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.network.ManagerService;
import com.infinity.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class PlayerTaskManager {
    private PlayerTaskManager() {
    }

    private static class PlayerTaskHolder {
        private static final PlayerTaskManager INSTANCE = new PlayerTaskManager();
    }

    public static PlayerTaskManager getInstance() {
        return PlayerTaskManager.PlayerTaskHolder.INSTANCE;
    }

    /***
     * 执行指定任务
     * @param playerId 玩家ID
     * @param taskId 任务ID
     * @param taskPrams 做任务的结果：
     * @return
     */
    public boolean doTask(long playerId, int taskId, Object... taskPrams) {
        return new PlayerTask(playerId, taskId, false, taskPrams).doTask();
    }

    /***
     * 执行指定任务
     * @param playerId 玩家ID
     * @param config 任务类型
     * @param taskPrams 任务结果
     * @return
     */
    public boolean doTask(long playerId, TaskCfg config, boolean isGm, Object... taskPrams) {
        try {
            return new PlayerTask(playerId, config, isGm, taskPrams).doTask();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 根据场景类型执行任务
     * @param playerId 玩家ID
     * @param taskTypeEnum 任务类型
     * @param taskPrams 任务结果
     * @return
     */
    public boolean doTask(long playerId, TaskTypeEnum taskTypeEnum, Object... taskPrams) {
        AtomicBoolean result = new AtomicBoolean(true);
        try {
            List<TaskCfg> configList = GameConfigManager.getInstance().getTaskDataManager().getConfigWithTaskType(taskTypeEnum.getCode());
            configList.forEach(config -> {
                if (!this.doTask(playerId, config, false, taskPrams)) {
                    result.set(false);
                }
            });
        } catch (Exception e) {
            result.set(false);
            log.error("PlayerTaskManager doTask error", e);
        }
        return result.get();
    }

    /***
     * 根据场景类型执行任务
     * @param playerId 玩家ID
     * @param taskTypeEnum 任务类型
     * @param taskPrams 任务结果
     * @return
     */
    public boolean task(long playerId, TaskTypeEnum taskTypeEnum, Boolean isGm, Object... taskPrams) {
        AtomicBoolean result = new AtomicBoolean(true);
        try {
            List<TaskCfg> configList = GameConfigManager.getInstance().getTaskDataManager().getConfigWithTaskType(taskTypeEnum.getCode());
            configList.forEach(config -> {
                if (!this.doTask(playerId, config, isGm, taskPrams)) {
                    result.set(false);
                }
            });
        } catch (Exception e) {
            result.set(false);
            log.error("PlayerTaskManager doTask error", e);
        }
        return result.get();
    }

    /***
     * 领取任务奖励
     * @param playerId 玩家ID
     * @param taskId 任务ID
     * @return true:成功，false:失败
     */
    public Response<ReceiveResponse> receive(long playerId, int taskId) {
        return new PlayerTask(playerId, taskId, false, null).receive();
    }

    /***
     * 一键领取任务奖励
     * @param playerId 玩家ID
     * @param type 任务类型
     * @param subType 任务子类型
     * @return true:成功，false:失败
     */
    public Response<ReceiveResponse> receive(long playerId, int type, int subType) {
        return new ReceiveAward().oneKeyReceive(playerId, type, subType);
    }

    /***
     * 任务变动提醒玩家
     * @param playerId 玩家ID
     * @param dataList 变动的任务数据
     */
    public void playerTaskChangeNotify(final Long playerId, List<TaskChangeNotifyTask.NotifyData> dataList) {
        if (ObjectUtils.isEmpty(dataList)) {
            return;
        }

        ITask mailTask = new TaskChangeNotifyTask(playerId, dataList);
        mailTask.init();
        ManagerService.getTaskManager().add(mailTask);
    }
}