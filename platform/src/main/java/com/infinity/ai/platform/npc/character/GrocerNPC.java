package com.infinity.ai.platform.npc.character;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.event.EventType;
import com.infinity.ai.platform.npc.goap.action.*;

import java.util.HashSet;
import java.util.Set;

//杂货店主NPC
public class GrocerNPC extends NPC {
    public GrocerNPC(Long id, String name) {
        super(id, name);
    }

    @Override
    protected void init() {
        //启动增加当前正在做的行为
        super.initAction();
    }

    //注册NPC行为
    protected void registAction() {
        this.addAction(new SaleAction(null));
        this.addAction(new BuyAction(null));
        this.addAction(new CookAction(null));
        this.addAction(new DinningAction(null));
        this.addAction(new SleepAction(null));
        this.addAction(new SpeakAction(null));
        this.addAction(new GetUpAction(null));
        this.addAction(new SpeakAction(null));
        this.addAction(new MoveToAction(null));
    }

    @Override
    protected CharacterType npcType() {
        return CharacterType.Grocer;
    }

    @Override
    public Set<EventType> register() {
        Set<EventType> objects = new HashSet<>();
        objects.add(EventType.NPC_APPROACH);
        objects.add(EventType.TIME_CHANGE);
        return objects;
    }
}
