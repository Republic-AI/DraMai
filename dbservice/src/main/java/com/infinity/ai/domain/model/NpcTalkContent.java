package com.infinity.ai.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NpcTalkContent {
    //发送者NPCID
    public Long sender;
    //接收者NPCID
    public Long target;
    //123123123
    public Long time;
    //说话内容
    public String content;
}
