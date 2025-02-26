package com.infinity.ai.platform.npc.goap.action.data;

import com.infinity.common.msg.chat.ChatData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyChatData extends BaseData {
    //回复的内容
    private String content;
    //回复的chatData
    private ChatData chatData;
}
