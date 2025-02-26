package com.infinity.ai.platform.event.task.handler;


import com.infinity.ai.platform.manager.PlayerTaskDataManager;
import com.infinity.ai.platform.manager.PlayerTaskManager;
import com.infinity.ai.platform.manager.RepositoryUtils;
import com.infinity.ai.platform.task.task.TaskChangeNotifyTask;
import com.infinity.ai.domain.model.PlayerTaskEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskUnlock {

    /***
     * 任务做完通知端上
     */
    protected void playerTaskChangeNotify(long playerId, List<PlayerTaskEntity> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return;
        }

        List<TaskChangeNotifyTask.NotifyData> dataList = new ArrayList<>();
        entityList.forEach(entity -> {
            if (entity.getUnlock() != null && entity.getUnlock() == 1) {
                TaskChangeNotifyTask.NotifyData data = new TaskChangeNotifyTask.NotifyData();
                data.setTaskId((int) entity.getTaskId());
                data.setStatus(entity.getStatus());
                data.setValue(entity.getValue1());
                dataList.add(data);
            }
        });

        PlayerTaskManager.getInstance().playerTaskChangeNotify(playerId, dataList);
    }

    /***
     * 任务做完通知端上
     */
    protected void taskChangeNotify(long playerId, List<Integer> taskIdList) {
        if (taskIdList == null || taskIdList.size() == 0) {
            return;
        }

        PlayerTaskDataManager taskRepository = RepositoryUtils.getBean(playerId);
        //PlayerTaskRepository taskRepository = SpringContextHolder.getBean(PlayerTaskRepository.class);
        playerTaskChangeNotify(playerId, taskRepository.findByPlayerIdTaskIds(playerId, taskIdList));
    }
}
