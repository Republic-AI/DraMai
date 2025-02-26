package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.NpcCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NpcCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, NpcCfg> config = new ConcurrentHashMap<>();

    public NpcCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        NpcCfg cfg = value.toJavaObject(NpcCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public NpcCfg get(final int id) {
        return config.get(id);
    }

    public List<NpcCfg> allNpcCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}