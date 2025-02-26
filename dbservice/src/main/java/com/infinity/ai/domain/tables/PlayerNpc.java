package com.infinity.ai.domain.tables;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

//玩家拥有的NPC
@Data
public class PlayerNpc {

    //玩家所有的NPC信息
    private Set<Long> npcIds = new HashSet<>();

    //当前正在操作的NPC
    private Long npcId;
}
