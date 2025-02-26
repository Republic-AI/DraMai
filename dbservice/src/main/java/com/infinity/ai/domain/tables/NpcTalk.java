package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.NpcTalkContent;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class NpcTalk {
    //是否在对话中
    private boolean isTalking;
    //我讲的对话内容,只保存最新说的一条记录, key=对方NPCID，value=我说的内容
    private Map<Long, NpcTalkContent> toMe = new HashMap<>();
    //我对其他NPC说的内容，key=其他NPCID，value=说话内容
    private Map<Long, NpcTalkContent> meSay = new HashMap<>();
    //对方对NPC说的次数，key=对方NPCID，value=次数
    private Map<Long, Integer> count = new HashMap<>();
}
