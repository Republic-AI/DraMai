package com.infinity.ai.platform.map.event;

import com.infinity.ai.NMap;
import com.infinity.ai.domain.model.MapObj;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.common.msg.platform.npcdata.Surroundings;
import com.infinity.db.db.DBManager;
import lombok.extern.slf4j.Slf4j;

//靠近事件
@Slf4j
public class NearEvent implements MapEvent {
    //一个格子32，5个格子距离范围内
    public static final long DISTANCE = 32 * 5;

    @Override
    public void onTrigger(MapObject mapObject, NPC npc) {
        try {
            log.debug("onTrigger nearEvent: MapObject ID={},NPCID={}", mapObject.getName(), npc.getId());
            if (mapObject.distanceTo(npc) <= DISTANCE) {

                //NMap nMap = DBManager.get(NMap.class, NMap.MAPID());
                //MapObj obj = nMap.get_v().getMapObject().getObjMap().get(mapObject.getName());
                //String status = (obj != null && obj.getState() == 1) ? "占用" : "空闲";

                Surroundings.Item item = Surroundings.Item.builder()
                        .objName(mapObject.name)
                        .oid(mapObject.name)
                        .type(mapObject.type)
                        /*.status(status)
                        .X(mapObject.x)
                        .Y(mapObject.y)
                        .availableActions()*/
                        .build();
                NpcData npcData = npc.getNpcDataListener().npcData;
                Surroundings surroundings = npcData.surroundings;
                surroundings.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}