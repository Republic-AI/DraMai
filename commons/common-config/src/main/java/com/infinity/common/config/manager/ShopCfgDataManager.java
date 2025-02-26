package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.ShopCfg;
import com.infinity.common.config.data.TaskCfg;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ShopCfgDataManager extends AbstractBaseDataManager {
    private final Map<Integer, ShopCfg> configs = new ConcurrentHashMap<>();

    public ShopCfgDataManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        int id = Integer.parseInt(key);
        ShopCfg cfg = value.toJavaObject(ShopCfg.class);
        configs.put(id, cfg);
    }

    public ShopCfg getShopCfgWithID(final int id) {
        return configs.get(id);
    }

    public Map<Integer, ShopCfg> getConfigs() {
        return this.configs;
    }

    public List<ShopCfg> getConfigList() {
        return configs.values().stream()
                .sorted(Comparator.comparing(ShopCfg::getPosition))
                .collect(Collectors.toList());
    }
}
