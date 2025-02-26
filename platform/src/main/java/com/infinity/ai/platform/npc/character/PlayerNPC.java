package com.infinity.ai.platform.npc.character;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.event.EventType;
import com.infinity.ai.platform.npc.goap.action.MoveToAction;

import java.util.HashSet;
import java.util.Set;

//玩家NPC
public class PlayerNPC extends NPC {
    public PlayerNPC(Long id, String name) {
        super(id, name);
        npcType();
    }

    @Override
    protected void init() {

    }

    @Override
    protected CharacterType npcType() {
        return CharacterType.Player;
    }

    @Override
    protected void registAction() {
        this.addAction(new MoveToAction(null));
    }

    @Override
    public Set<EventType> register() {
        Set<EventType> objects = new HashSet<>();
        objects.add(EventType.NPC_APPROACH);
        objects.add(EventType.TIME_CHANGE);
        return objects;
    }

}
