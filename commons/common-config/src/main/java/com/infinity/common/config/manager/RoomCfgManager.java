package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.JoinCfg;
import com.infinity.common.config.data.RoomCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, RoomCfg> config = new ConcurrentHashMap<>();

    public RoomCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        RoomCfg cfg = value.toJavaObject(RoomCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public RoomCfg get(final int id) {
        return config.get(id);
    }

    public List<RoomCfg> getAllJoinCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}