package com.infinity.ai.platform.map.object.prop;

import lombok.Data;

@Data
public class FarmingProp {
    //种植的物品
    public Integer itemId;
    //种植的物品数量
    public Integer count;

    /*public static Integer getItemId(Map<String, Object> prop) {
        Object value = getProp(itemId, prop);
        return value == null ? null : (int) value;
    }

    public static Integer getCount(Map<String, Object> prop) {
        Object value = getProp(count, prop);
        return value == null ? 0 : (int) value;
    }

    public static Object getProp(String key, Map<String, Object> prop) {
        if (prop == null || prop.size() == 0) {
            return null;
        }
        return prop.get(key);
    }*/
}
