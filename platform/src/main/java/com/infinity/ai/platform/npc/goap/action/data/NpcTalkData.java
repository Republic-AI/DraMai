package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NpcTalkData extends BaseData {
    //发送者NPCID
    public Long sender;
    //发送则名称
    public String sName;
    //接收者NPCID
    public Long npcId;
    //接受者名称
    public String tName;
    //123123123
    public Long time;
    //说话内容
    public String content;
    //是否是发起者
    public boolean initiator;
    //谈话地点
    public int gridX;
    public int girdY;
    //是否是结束对话
    public int endingTalk;
}
