package com.infinity.common.msg.platform.chat;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//聊天
public class PChatNpcRequest extends BaseMsg<PChatNpcRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.P_NPC_TALK_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.P_NPC_TALK_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //npc id
        public Long npcId;

        public ChatData chatData;
    }
}
