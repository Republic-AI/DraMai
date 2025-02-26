package com.infinity.ai.platform.map.object;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class MapData {
    //地图名称
    private String name;
    //地图宽度
    private int width;
    //地图高度
    private int height;
    //地图物品：外层key:NPC Id, 0为公共物品, 里面的key=物品类型
    private Map<Integer, Map<String, List<MapObject>>> objectgroup;

    //键值对存储
    private Map<String, MapObject> objectMap = new HashMap<>();

    public void addObj(MapObject mapObject) {
        objectMap.put(mapObject.getName(), mapObject);
    }

    public MapObject getObject(String objId) {
        return this.objectMap.get(objId);
    }

    public List<MapObject> getByNpcIdType(Integer npcId, String type) {
        Map<String, List<MapObject>> npcObjMap = this.objectgroup.get(npcId);
        if (npcObjMap == null || npcObjMap.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return npcObjMap.get(type);
    }

    public Map<String, List<MapObject>> getByNpcId(Integer npcId) {
        return objectgroup.get(npcId);
    }

    public List<MapObject> allObject(){
        return objectMap.values().stream().collect(Collectors.toList());
    }
}