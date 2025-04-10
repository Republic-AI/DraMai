package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.DramaCfg;
import com.infinity.common.config.data.FurnitureCfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DramaCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, Map<Integer, List<DramaCfg>>> config = new HashMap<>();

    public DramaCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        DramaCfg cfg = value.toJavaObject(DramaCfg.class);
        Map<Integer, List<DramaCfg>> map = config.getOrDefault(cfg.getAnimationId(), new HashMap<>());
        config.put(cfg.getAnimationId(), map);
        List<DramaCfg> list = map.getOrDefault(cfg.getSection(), new ArrayList<>());
        map.put(cfg.getSection(), list);
        list.add(cfg);
    }

    public Map<Integer, List<DramaCfg>> getAnimationCfg(int animationId) {
        return config.get(animationId);
    }
}