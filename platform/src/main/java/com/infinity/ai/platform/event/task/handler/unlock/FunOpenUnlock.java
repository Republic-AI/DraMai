package com.infinity.ai.platform.event.task.handler.unlock;

import com.infinity.ai.platform.manager.PlayerTaskDataManager;
import com.infinity.ai.platform.manager.RepositoryUtils;
import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.platform.event.task.Unlock;
import com.infinity.ai.platform.event.task.handler.TaskUnlock;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;


import java.util.List;
import java.util.stream.Collectors;

/***
 * 功能ID开启解锁
 * 关联FunctionOpening表ID
 */
public class FunOpenUnlock extends TaskUnlock implements Unlock {

    //private final PlayerTaskRepository taskRepository;

    public FunOpenUnlock() {
        //taskRepository = SpringContextHolder.getBean(PlayerTaskRepository.class);
    }

    @Override
    public boolean isUnlock(long playerId, TaskCfg taskConfig) {
        return true;
    }

    @Override
    public int unlock(long playerId, int unlockValue, TaskCfg taskConfig) {
        List<TaskCfg> configList = GameConfigManager.getInstance().getTaskDataManager()
                .getConfigWithUnlock(taskConfig.getUnlock(), unlockValue);

        if (configList == null || configList.size() == 0) {
            return 0;
        }

        if(!isUnlock(playerId, taskConfig)){
            return -1;
        }

        PlayerTaskDataManager taskRepository = RepositoryUtils.getBean(playerId);
        List<Integer> taskIdList = configList.stream().map(TaskCfg::getId).distinct().collect(Collectors.toList());
        List<PlayerTaskEntity> taskEntities = taskRepository.findByPlayerIdTaskIdsStatus(playerId, taskIdList);
        int state = taskRepository.unlockByTaskId(playerId, taskIdList);
        playerTaskChangeNotify(playerId, taskEntities);
        return state;
    }
}
