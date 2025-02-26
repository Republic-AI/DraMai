package com.infinity.ai.platform.event.task.handler.unlock;

import com.infinity.ai.platform.manager.PlayerTaskDataManager;
import com.infinity.ai.platform.manager.RepositoryUtils;
import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.platform.event.task.Unlock;
import com.infinity.ai.platform.event.task.enums.TaskStatusEnum;
import com.infinity.ai.platform.event.task.handler.TaskUnlock;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;

import java.util.List;
import java.util.stream.Collectors;

/***
 * 3.完成某个任务ID解锁该条任务
 */
public class FinishTaskUnlock extends TaskUnlock implements Unlock {

    public FinishTaskUnlock() {
    }

    @Override
    public boolean isUnlock(long playerId, TaskCfg taskConfig) {
        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerId);
        PlayerTaskEntity entity = repository.findByPlayerIdTaskId(playerId, taskConfig.getUnlockvalue());
        return (entity != null && entity.getStatus() == TaskStatusEnum.FINISH.getCode());
    }

    @Override
    public int unlock(long playerId, int unlockValue, TaskCfg taskConfig) {
        List<TaskCfg> configList = GameConfigManager.getInstance().getTaskDataManager()
                .getConfigWithUnlock(taskConfig.getUnlock(), unlockValue);

        if (configList == null || configList.size() == 0) {
            return 0;
        }

        PlayerTaskDataManager repository = RepositoryUtils.getBean(playerId);
        List<Integer> taskIdList = configList.stream().map(TaskCfg::getId).distinct().collect(Collectors.toList());
        int state = repository.unlockByTaskId(playerId, taskIdList);
        List<PlayerTaskEntity> entityList = repository.findByPlayerIdTaskIds(playerId, taskIdList);
        playerTaskChangeNotify(playerId, entityList);
        return state;
    }

}
