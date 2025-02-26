package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpeakData extends BaseData {
    //说话内容：
    //private List<TalkContent> content;

    //发送者NPCID
    public Long sender;
    //发送则名称
    public String sName;
    //接收者NPCID
    public Long target;
    //接受者名称
    public String tName;
    //123123123
    public Long time;
    //说话内容
    public String content;
}
