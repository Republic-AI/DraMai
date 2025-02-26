package com.infinity.ai.platform.map.event;

import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;

//显示对话框的事件
public class DialogEvent implements MapEvent {
    private String dialogText;

    public DialogEvent(String dialogText) {
        this.dialogText = dialogText;
    }

    @Override
    public void onTrigger(MapObject mapObject, NPC npc) {
        System.out.println("Showing dialog: " + dialogText);
        // 实现显示对话框的逻辑
    }
}