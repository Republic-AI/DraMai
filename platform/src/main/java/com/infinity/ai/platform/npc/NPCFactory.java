package com.infinity.ai.platform.npc;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.npc.character.*;

import static com.infinity.ai.platform.npc.character.CharacterType.getByCode;

public class NPCFactory {
    //创建NPC
    public static NPC createNPC(PNpc npc) {
        CharacterType type = getByCode(npc.getType()).get();
        if (type == null) {
            return null;
        }

        switch (type) {
            case Player:
                return new PlayerNPC(npc.getId(), npc.getName());
            case Farmer:
                return new FarmerNPC(npc.getId(), npc.getName());
            case Grocer:
                return new GrocerNPC(npc.getId(), npc.getName());
            case Herdman:
                return new HerdmanNPC(npc.getId(), npc.getName());
            case Baker:
                return new BakerNPC(npc.getId(), npc.getName());
            case Satoshi:
                return new SatoshiNPC(npc.getId(), npc.getName());
            case Pepe:
                return new PepeNPC(npc.getId(), npc.getName());
            case Popcat:
                return new PopcatNPC(npc.getId(), npc.getName());
            case Musk:
                return new MuskNPC(npc.getId(), npc.getName());
            case Pippin:
                return new PippinNPC(npc.getId(), npc.getName());
            case Eliza:
                return new ElizaNPC(npc.getId(), npc.getName());
            case Trump:
                return new TrumpNPC(npc.getId(), npc.getName());
            case Morpheos:
                return new MorpheosNPC(npc.getId(), npc.getName());
            case Ava:
                return new AvaNPC(npc.getId(), npc.getName());
            case Luna:
                return new LunaNPC(npc.getId(), npc.getName());
            case Ivy:
                return new IvyNPC(npc.getId(), npc.getName());
            case Kai:
                return new KaiNPC(npc.getId(), npc.getName());
            case Leo:
                return new LeoNPC(npc.getId(), npc.getName());
            case Nova:
                return new NovaNPC(npc.getId(), npc.getName());
            case Aiden:
                return new AidenNPC(npc.getId(), npc.getName());
            case Selena:
                return new QiuNPC(npc.getId(), npc.getName());
            default:
                throw new IllegalArgumentException("Unknown npc type: " + type);
        }
    }
}
