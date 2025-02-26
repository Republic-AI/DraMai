package com.infinity.ai.domain.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//地图基础数据
@Data
public class MapObj {
    //地图物品ID
    private String id;
    //地图物品状态：ObjectStatus.FarmingObjType
    private Integer state;
    //物品的位置：X
    private Integer y;
    //物品的位置：Y
    private Integer x;
    //属性:com.infinity.ai.platform.map.object.prop.FarmingProp
    private Map<String, Object> prop;

    public static MapObj buildMapObj() {
        MapObj obj = new MapObj();
        Map<String, Object> prop = new HashMap<>();
        obj.setProp(prop);
        return obj;
    }
}
