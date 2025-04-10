package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.common.config.data.RoomCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.RoomCfgManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoomManager {

    @Getter
    private static final RoomManager instance = new RoomManager();

    @Getter
    @Setter
    private Map<Integer, NpcRoom> roomMap = new ConcurrentHashMap<>();

    public void init() {
        RoomCfgManager roomCfgManager = GameConfigManager.getInstance().getRoomCfgManager();
        for (RoomCfg roomCfg : roomCfgManager.getAllJoinCfg()) {
            //初始话房间地图
            GameMap gameMap = new GameMap(roomCfg.getMap());
            NpcRoom npcRoom = new NpcRoom(roomCfg.getId());
            npcRoom.setGameMap(gameMap);
            roomMap.put(roomCfg.getId(), npcRoom);
        }
    }

    public NpcRoom getRoom(int roomId) {
        return roomMap.get(roomId);
    }

    public MapManager getMapManager(int roomId) {
        return roomMap.get(roomId).getMapManager();
    }
}
