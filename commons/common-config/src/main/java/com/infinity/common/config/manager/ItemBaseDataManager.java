package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.ItemCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ItemBaseDataManager extends AbstractBaseDataManager {

    private final Map<Integer, ItemCfg> configs_ = new ConcurrentHashMap<>();

    private final Map<String, Integer> itemNameMap_ = new ConcurrentHashMap<>();

    public ItemBaseDataManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        int id = Integer.parseInt(key);
        ItemCfg cfg = value.toJavaObject(ItemCfg.class);
        configs_.put(id, cfg);
        itemNameMap_.put(cfg.getName(), id);
    }

    public ItemCfg getItemConfigWithID(final int itemID) {
        return configs_.get(itemID);
    }

    public ItemCfg getItemConfigWithName(final String name) {
        Integer id = itemNameMap_.get(name);
        if (id == null) {
            return null;
        }
        return configs_.get(id);
    }

    public List<ItemCfg> getConfigWithKind(final Integer type) {
        return configs_.values().stream()
                .filter(x -> (x.getKind() == type))
                .collect(Collectors.toList());
    }
}
