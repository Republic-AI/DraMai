package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.TeaseCatCfg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeaseCatCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, TeaseCatCfg> config = new ConcurrentHashMap<>();

    public TeaseCatCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        TeaseCatCfg cfg = value.toJavaObject(TeaseCatCfg.class);
        config.put(cfg.getItemId(), cfg);
    }

    public TeaseCatCfg get(final int itemId) {
        return config.get(itemId);
    }
}