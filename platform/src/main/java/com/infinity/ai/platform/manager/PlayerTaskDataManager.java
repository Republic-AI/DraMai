package com.infinity.ai.platform.manager;


import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.domain.model.PlayerTaskReceiveEntity;
import com.infinity.ai.domain.tables.PlayerTask;
import com.infinity.common.config.data.TaskCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.TaskDataManager;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerTaskDataManager {

    private final Player owner;

    public PlayerTask getPlayerTask() {
        return this.owner.getPlayerModel().get_v().getTask();
    }

    public PlayerTaskDataManager(Player player) {
        this.owner = player;
    }

    public int save(PlayerTaskEntity entity) {
        this.getPlayerTask().getTaskMap().put((int) entity.getTaskId(), entity);
        return 1;
    }

    public int batchSaveReceive(List<PlayerTaskReceiveEntity> receiveList) {
        //暂不记录领取记录
        return 0;
    }

    public PlayerTaskEntity findByPlayerIdTaskId(long playerId, int taskId) {
        return this.getPlayerTask().getTaskMap().get(taskId);
    }

    public int updateValueAndStatus(PlayerTaskEntity entity) {
        return save(entity);
    }

    public List<PlayerTaskEntity> findByPlayerIdTypeSubTypeStatus(long playerId, long type, long subType) {
        List<PlayerTaskEntity> entities = new ArrayList<>();
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        TaskDataManager manager = GameConfigManager.getInstance().getTaskDataManager();
        taskMap.forEach((k, v) -> {
            TaskCfg taskConfig = Optional.ofNullable(manager.getConfigWithID((int) v.getTaskId())).orElse(new TaskCfg());
            if (taskConfig.getPosition() == type
                    && taskConfig.getMinPosition() == subType
                    && v.getStatus() == 2
                    && v.getUnlock() == 1) {
                entities.add(v);
            }
        });

        return entities;
    }

    public List<PlayerTaskEntity> findByPlayerIdTaskIds(long playerId, List<Integer> taskIds) {
        List<PlayerTaskEntity> entities = new ArrayList<>();
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        taskIds.forEach(taskId -> {
            PlayerTaskEntity entity = taskMap.get(taskId);
            if (entity != null) {
                entities.add(entity);
            }
        });

        return entities;
    }

    public List<PlayerTaskEntity> findByPlayerIdTaskIdsStatus(long playerId, List<Integer> taskIds) {
        List<PlayerTaskEntity> entities = new ArrayList<>();
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        taskIds.forEach(taskId -> {
            PlayerTaskEntity entity = taskMap.get(taskId);
            if (entity != null && entity.getUnlock() == 0) {
                entities.add(entity);
            }
        });

        return entities;
    }

    public int deleteTaskByRefurbish(int refurbish) {
        TaskDataManager manager = GameConfigManager.getInstance().getTaskDataManager();
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        Iterator<Map.Entry<Integer, PlayerTaskEntity>> iter = taskMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, PlayerTaskEntity> next = iter.next();
            Integer taskId = next.getKey();
            TaskCfg taskConfig = manager.getConfigWithID(taskId);
            if (taskConfig == null || taskConfig.getRefurbish() == refurbish) {
                iter.remove();
            }
        }
        return 0;
    }

    public int deleteReceiveByRefurbish(int refurbish) {
        //暂不记录领取记录
        return 0;
    }

    public int updateStatusByIds(List<Long> idList) {
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        Collection<PlayerTaskEntity> values = taskMap.values();
        if (values != null) {
            List<PlayerTaskEntity> collect = values.stream().filter(v -> idList.stream().anyMatch(id -> v.getId().equals(id))).collect(Collectors.toList());
            collect.forEach(taskEntity -> {
                PlayerTaskEntity entity = taskMap.get((int) taskEntity.getTaskId());
                if (entity != null && entity.getStatus() == 2) {
                    entity.setStatus(3);
                }
            });
        }

        return 0;
    }

    public int unlock(List<Long> idList) {
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        Collection<PlayerTaskEntity> values = taskMap.values();
        if (values != null) {
            List<PlayerTaskEntity> collect = values.stream().filter(v -> idList.stream().anyMatch(id -> v.getId().equals(id))).collect(Collectors.toList());
            collect.forEach(taskEntity -> {
                PlayerTaskEntity entity = taskMap.get((int) taskEntity.getTaskId());
                if (entity != null) {
                    entity.setUnlock(1);
                }
            });
        }
        return 1;
    }

    public int unlockByTaskId(long playerId, List<Integer> taskIdList) {
        Map<Integer, PlayerTaskEntity> taskMap = this.getPlayerTask().getTaskMap();
        taskIdList.forEach(taskId -> {
            PlayerTaskEntity entity = taskMap.get(taskId);
            if (entity != null && entity.getUnlock() == 0) {
                entity.setUnlock(1);
            }
        });
        return 1;
    }
}
