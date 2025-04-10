package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.FurnitureCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FurnitureCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, FurnitureCfg> config = new ConcurrentHashMap<>();

    public FurnitureCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        FurnitureCfg cfg = value.toJavaObject(FurnitureCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public FurnitureCfg get(final int id) {
        return config.get(id);
    }

    public List<FurnitureCfg> getAllJoinCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}