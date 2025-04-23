package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.JoinCfg;
import com.infinity.common.config.data.RelationCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RelationCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, RelationCfg> config = new ConcurrentHashMap<>();

    public RelationCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        RelationCfg cfg = value.toJavaObject(RelationCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public RelationCfg get(final int id) {
        return config.get(id);
    }

    public List<RelationCfg> getAllRelationCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}