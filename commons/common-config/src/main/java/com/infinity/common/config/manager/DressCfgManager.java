package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.DressCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DressCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, DressCfg> config = new ConcurrentHashMap<>();

    public DressCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        DressCfg cfg = value.toJavaObject(DressCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public DressCfg get(final int id) {
        return config.get(id);
    }

    public List<DressCfg> getAllJoinCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}