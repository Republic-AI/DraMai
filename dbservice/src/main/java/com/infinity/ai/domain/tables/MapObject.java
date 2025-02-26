package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.MapObj;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//地图基础数据
@Data
public class MapObject {

    //地图对象机会，key=地图物品ID
    public Map<String, MapObj> objMap  = new HashMap<>();
}
