package com.infinity.ai.domain.tables;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

//NPC live
@Data
public class PlayerLive {

    //当前正在哪个NPC
    private Long roomId;
}
