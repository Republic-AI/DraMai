package com.infinity.ai.platform.map.event;

import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;

//播放音效的事件
public class SoundEvent implements MapEvent {
    private String soundName;

    public SoundEvent(String soundName) {
        this.soundName = soundName;
    }

    @Override
    public void onTrigger(MapObject mapObject, NPC npc) {
        System.out.println("Playing sound: " + soundName);
        // 实现播放音效的逻辑
    }
}
