package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.NpcTypeCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NpcTypeCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, NpcTypeCfg> config = new ConcurrentHashMap<>();

    public NpcTypeCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        NpcTypeCfg cfg = value.toJavaObject(NpcTypeCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public NpcTypeCfg get(final int id) {
        return config.get(id);
    }

    public List<NpcTypeCfg> allNpcTypeCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}