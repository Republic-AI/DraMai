package com.infinity.common.msg.chat;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//聊天返回消息
public class ChatResponse extends BaseMsg<ChatResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_CHAT;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kChatCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kChatCommand;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {

        private Long npcId;

        private ChatData chatData;
    }
}
