package com.infinity.ai.platform.npc.goap.action.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TalkContent {
    //发送者NPCID
    public String sender;
    //接收者NPCID
    public String target;
    //123123123
    public Long time;
    //说话内容
    public String content;
}
