package com.infinity.ai.platform.manager;

import com.infinity.ai.NMap;
import com.infinity.ai.domain.model.MapObj;
import com.infinity.ai.domain.tables.MapWorldData;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.utils.StringUtils;
import com.infinity.db.db.DBManager;
import lombok.Getter;

public class MapDataManager {
    //游戏中一天等于8小时
    public static final long DAY = 2 * 60 * 1000; //8 * 60 * 60 * 1000;

    @Getter
    private GameMap gameMap;

    //解析游戏地图数据
    public void createGameMap() {
        //gameMap = new GameMap();
    }

    //初始化游戏数据
    public void init() {
        NMap nMap = DBManager.get(NMap.class, NMap.MAPID());
        if (nMap == null) {
            nMap = new NMap();
            nMap.setId(NMap.MAPID());
            nMap.setName("游戏物品数据");

            //游戏时间
            MapWorldData worldData = nMap.get_v().getWorldData();
            worldData.setTime(System.currentTimeMillis());
            DBManager.add(nMap);
        }
    }

    public NMap getMap() {
        return DBManager.get(NMap.class, NMap.MAPID());
    }

    //获取游戏时间
    public Long getGameTime() {
        Long gameStartTime = getMap().get_v().getWorldData().getTime();
        long interval = System.currentTimeMillis() - gameStartTime;
        return gameStartTime + interval * GameConsts.TIME_SPEED;
    }

    //根据游戏时间推算系统时间
    public static Long getTimeFromGameTime(long gameTime){
        long now = System.currentTimeMillis();
        long interval = gameTime - now;
        return now + (interval / GameConsts.TIME_SPEED);
    }

    public void update(String id, Integer state, boolean notify) {
        if (StringUtils.isEmpty(id) || state == null) {
            return;
        }

        MapObj obj = this.getMap().get_v().getMapObject().getObjMap().get(id);
        if (obj != null) {
            obj.setState(state);
            NpcManager.getInstance().getListener().updateItem(obj.getId(), state);
        }
    }

    private static class Holder {
        private static final MapDataManager kInstance = new MapDataManager();
    }

    public static MapDataManager getInstance() {
        return Holder.kInstance;
    }
}
