package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.entity.MapItemData;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 地图数据
 */
@Data
public class VMap {
    //地图对象
    private MapObject mapObject = new MapObject();
    //地图世界数据
    private MapWorldData worldData = new MapWorldData();

    private Map<Integer, CopyOnWriteArrayList<MapItemData>> mapItemData = new ConcurrentHashMap<>();

    private Map<Integer, String> bannerConfig = new HashMap<>();

    private Map<String, String> relationConfig = new HashMap<>();
}
