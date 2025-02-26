package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.GiftCfg;
import com.infinity.common.config.data.JoinCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JoinCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, JoinCfg> config = new ConcurrentHashMap<>();

    public JoinCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        JoinCfg cfg = value.toJavaObject(JoinCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public JoinCfg get(final int id) {
        return config.get(id);
    }

    public List<JoinCfg> getAllJoinCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}