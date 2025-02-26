package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.NpcActionCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NpcActionCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, NpcActionCfg> config = new ConcurrentHashMap<>();

    public NpcActionCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        NpcActionCfg cfg = value.toJavaObject(NpcActionCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public NpcActionCfg get(final int id) {
        return config.get(id);
    }

    public List<NpcActionCfg> allNpcActionCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}