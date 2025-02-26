package com.infinity.ai.domain.tables;

import lombok.Data;

/**
 * 地图数据
 */
@Data
public class VMap {
    //地图对象
    private MapObject mapObject = new MapObject();
    //地图世界数据
    private MapWorldData worldData = new MapWorldData();

}
