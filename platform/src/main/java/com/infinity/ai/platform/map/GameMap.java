package com.infinity.ai.platform.map;

import com.infinity.ai.platform.application.Config;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.event.MapEventManager;
import com.infinity.ai.platform.map.object.MapData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.common.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GameMap {
    //地图数据
    public MapData mapData;
    // 地图表示，0表示可通行，1表示障碍物
    public final int[][] map;

    public final List<Position> passableList = new ArrayList<>();
    //地图事件管理器
    public final MapEventManager eventManager;
    //A*寻址算法
    public AStar aStar;

    public GameMap(String mapName) {
        //加载地图坐标数据
        String gameDataPath = Config.getInstance().getGameDataPath();
        map = MapLoader.parseMap(gameDataPath + "/" + mapName + ".tmx");
        int index = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 0) {
                    passableList.add(new Position(i, j));
                }
                index++;
            }
        }
        Collections.shuffle(passableList);
        aStar = new AStar(map);
        try {
            //加载地图数据
            this.mapData = MapLoader.mapParser(gameDataPath + "/" + mapName + ".json");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("loadMap data error......");
            System.exit(-1);
        }

        eventManager = new MapEventManager(mapData);
    }

    public MapObject getObject(String objId) {
        return mapData.getObject(objId);
    }

    public List<MapObject> getByNpcIdType(Integer npcId, String type) {
        return mapData.getByNpcIdType(npcId, type);
    }

    public Map<String, List<MapObject>> getByNpcId(Integer npcId){
        return mapData.getByNpcId(npcId);
    }

    public Position randomPassablePostion(Position position, int distance) {
        Position ret = null;
        int count = 0;
        List<Position> list = MapDataManager.getInstance().getGameMap().passableList;
        while ((ret == null || MapUtil.getDistance(position, ret) < distance) && count < 100) {
            ret = list.get(RandomUtils.randNum(0, list.size() - 1));
            count++;
        }
        return ret;
    }

    public boolean isWalkable(Position position) {
        return map[position.getX()][position.getY()] == 0;
    }
}
