package com.infinity.ai.platform.event.task;

import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.platform.event.task.enums.TaskStatusEnum;
import com.infinity.ai.platform.manager.IDManager;
import com.infinity.common.base.common.Response;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.TaskDataManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 玩家任务
 */
public class PlayerTask {
    @Getter
    private Unlock unlock;
    @Getter
    private TaskHandler taskHandler;
    @Getter
    private TaskCfg taskConfig;
    @Getter
    private long playerId;
    //做任务结果值
    @Getter
    private final Object[] taskPrams;
    @Getter
    private final boolean isGm;

    public PlayerTask(long playerId, int taskId, boolean isGm, Object... taskPrams) {
        this.playerId = playerId;
        this.taskPrams = taskPrams;
        this.isGm = isGm;
        taskConfig = GameConfigManager.getInstance().getTaskDataManager().getConfigWithID(taskId);
        assert this.taskConfig != null : "taskConfig is null,please check taskId!";
        init();
    }

    public PlayerTask(long playerId, TaskCfg taskConfig, boolean isGm, Object... taskPrams) {
        this.playerId = playerId;
        this.taskPrams = taskPrams;
        this.isGm = isGm;
        this.taskConfig = taskConfig;
        assert this.taskConfig != null : "taskConfig is null";
        init();
    }

    private void init() {
        unlock = PlayerTaskContext.getUnlock(this.taskConfig.getUnlock());
        assert this.unlock != null;
        taskHandler = PlayerTaskContext.getTaskHandler(this.taskConfig.getTaskType());
        assert this.taskHandler != null;
    }

    public boolean doTask() {
        return taskHandler.execute(this);
    }

    public Response<ReceiveResponse> receive() {
        return taskHandler.receive(this);
    }

    public boolean isUnlock() {
        return this.unlock.isUnlock(this.playerId, this.taskConfig);
    }

    public boolean isUnlock(TaskCfg config) {
        return this.unlock.isUnlock(this.playerId, config);
    }

    public PlayerTaskEntity newEntity() {
        PlayerTaskEntity entity = new PlayerTaskEntity();
        entity.setId(IDManager.getInstance().nextId());
        entity.setTaskId(this.taskConfig.getId());
        entity.setValue1(0);
        entity.setValue2("");
        entity.setValue3("");
        entity.setUnlock(this.isUnlock() ? 1 : 0);//任务是否已解锁:0:未解锁,1:已解锁
        entity.setStatus(TaskStatusEnum.INPROGRESS.getCode());
        return entity;
    }

    @Deprecated
    public PlayerTaskEntity newEntity(TaskCfg config) {
        PlayerTaskEntity entity = new PlayerTaskEntity();
        entity.setId(IDManager.getInstance().nextId());
        entity.setTaskId(config.getId());
        entity.setValue1(0);
        entity.setValue2("");
        entity.setValue3("");
        entity.setUnlock(this.isUnlock(config) ? 1 : 0);//任务是否已解锁:0:未解锁,1:已解锁
        entity.setStatus(TaskStatusEnum.INPROGRESS.getCode());
        return entity;
    }

    @Deprecated
    public List<PlayerTaskEntity> buildEntityList() {
        List<PlayerTaskEntity> entityList = new ArrayList<>();
        TaskDataManager taskDataManager = GameConfigManager.getInstance().getTaskDataManager();
        Map<Integer, TaskCfg> all = taskDataManager.getAll();
        for (Map.Entry<Integer, TaskCfg> data : all.entrySet()) {
            entityList.add(newEntity(data.getValue()));
        }
        return entityList;
    }

}
