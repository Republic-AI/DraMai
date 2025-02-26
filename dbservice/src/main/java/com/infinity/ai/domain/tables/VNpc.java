package com.infinity.ai.domain.tables;

import lombok.Data;

/**
 * 对应Npc表
 */
@Data
public class VNpc {
    //NPC 钱包
    private PlayerBag bag = new PlayerBag();
    //NPC行为
    private NpcAction action = new NpcAction();
    //NPC 对话
    private NpcTalk talk = new NpcTalk();
}
