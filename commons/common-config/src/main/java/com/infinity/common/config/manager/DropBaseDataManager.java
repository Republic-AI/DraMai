package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.DropsCfg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DropBaseDataManager extends AbstractBaseDataManager {
    private final Map<Integer, DropsCfg> drops_config_ = new ConcurrentHashMap<>();

    public DropBaseDataManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        final int id = Integer.parseInt(key);
        final DropsCfg cfg = value.toJavaObject(DropsCfg.class);
        drops_config_.put(id, cfg);
    }

    public DropsCfg getDropsConfigWithID(final int dropID) {
        return drops_config_.get(dropID);
    }
}
