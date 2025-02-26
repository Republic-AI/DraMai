package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.TaskCfg;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/***
 * 玩家任务配置
 */
public class TaskDataManager extends AbstractBaseDataManager {

    private final Map<Integer, TaskCfg> configMap = new ConcurrentHashMap<>();

    public TaskDataManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(String key, JSONObject value) {
        int id = Integer.parseInt(key);
        TaskCfg cfg = value.toJavaObject(TaskCfg.class);
        assert !configMap.containsKey(id);
        configMap.put(id, cfg);
    }

    public Map<Integer, TaskCfg> getAll() {
        return configMap;
    }

    public TaskCfg getConfigWithID(final Integer taskId) {
        return configMap.get(taskId);
    }

    public List<TaskCfg> getConfigWithTaskType(final Integer taskType) {
        return configMap.values().stream()
                .filter(x -> x.getTaskType() == taskType)
                .sorted(Comparator.comparing(TaskCfg::getId))
                .collect(Collectors.toList());
    }

    public List<TaskCfg> getConfigWithUnlock(final Integer unlock, Integer unlockValue) {
        return configMap.values().stream()
                .filter(x -> (x.getUnlock() == unlock && x.getUnlockvalue() == unlockValue))
                .collect(Collectors.toList());
    }

    public List<TaskCfg> getConfigWithUnlockLg(final Integer unlock, Integer unlockValue) {
        return configMap.values().stream()
                .filter(x -> (x.getUnlock() == unlock && unlockValue >= x.getUnlockvalue()))
                .collect(Collectors.toList());
    }

    public List<TaskCfg> getConfigWithPosition(final Integer position) {
        if (position >= 0) {
            return configMap.values().stream()
                    .filter(x -> x.getPosition() == position)
                    .collect(Collectors.toList());
        }
        //-1查询所有
        return configMap.values().stream().collect(Collectors.toList());
    }

    public List<TaskCfg> getConfigWithPositionMinPosition(final Integer position, Integer minPostion) {
        return configMap.values().stream()
                .filter(x -> x.getPosition() == position && x.getMinPosition() == minPostion)
                .sorted(Comparator.comparing(TaskCfg::getId))
                .collect(Collectors.toList());
    }
}
