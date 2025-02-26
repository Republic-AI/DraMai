package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.NpcItemCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NpcItemCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, JSONObject> config = new ConcurrentHashMap<>();

    public NpcItemCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        //NpcItemCfg cfg = value.toJavaObject(NpcItemCfg.class);
        config.put(Integer.parseInt(key), value);
    }

    public JSONObject get(final int id) {
        return config.get(id);
    }

    public List<JSONObject> allNpcItemCfg() {
        return config.values().stream().collect(Collectors.toList());
    }
}