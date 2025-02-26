package com.infinity.ai.platform.map.event;

import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.MapData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.common.config.data.NpcCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.platform.npcdata.Surroundings;

import java.util.ArrayList;
import java.util.List;

// 地图事件管理器
public class MapEventManager {
    private MapData mapData;
    private List<MapObject> mapObjects;

    public MapEventManager(MapData mapData) {
        this.mapData = mapData;
        mapObjects = this.mapData.allObject();
    }

    @Deprecated
    public void addMapObject(MapObject mapObject) {
        if (mapObjects == null) {
            mapObjects = new ArrayList<>();
        }

        mapObjects.add(mapObject);
    }

    public void checkEvents(NPC npc) {
        /*if (mapObjects != null) {
            // 检查每个地图物件，是否需要触发事件
            for (MapObject obj : mapObjects) {
                obj.checkEventTrigger(npc);
            }
        }*/

        //检测附近的人
        checkNearNpc(npc);
    }

    public void checkNearNpc(NPC npc) {
        npc.getNpcDataListener().npcData.surroundings.clear();
        NpcManager npcManager = NpcManager.getInstance();
        List<NpcCfg> npcCfgs = GameConfigManager.getInstance().getNpcCfgManager().allNpcCfg();
        for (NpcCfg npcCfg : npcCfgs) {
            try {
                if (npcCfg.getType() == 0 || npc.getId().intValue() == npcCfg.getId()) {
                    continue;
                }

                NpcHolder npcHolder = npcManager.getOnlineNpcHolder(npcCfg.getId());
                if (npcHolder == null) {
                    continue;
                }

                NPC nearNpc = npcHolder.getNpc();
                if (nearNpc.distanceTo(npc) <= NearEvent.DISTANCE) {
                    ActionEnumType curAction = ActionEnumType.getByCode(nearNpc.getCurActionType()).orElse(null);
                    String status = curAction == null ?
                            "free" : curAction.getName();
                    Surroundings.People people = Surroundings.People.builder()
                            .npcId(nearNpc.getId())
                            .status(status)
                            .build();

                    npc.getNpcDataListener().npcData.surroundings.addPeople(people);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}