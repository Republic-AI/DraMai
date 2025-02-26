package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.GiftCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GiftCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, GiftCfg> config = new ConcurrentHashMap<>();

    public GiftCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        GiftCfg cfg = value.toJavaObject(GiftCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public GiftCfg get(final int id) {
        return config.get(id);
    }

    public List<GiftCfg> allGiftCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}