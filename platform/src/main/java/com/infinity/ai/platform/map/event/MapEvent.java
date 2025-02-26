package com.infinity.ai.platform.map.event;

import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;

// 地图事件接口
public interface MapEvent {

    //触发事件的方法
    void onTrigger(MapObject mapObject, NPC npc);
}