package com.infinity.common.msg.platform.chat;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

public class QueryNpcPlayerRequest extends BaseMsg<QueryNpcPlayerRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_PLAYER_NPC_CHAT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_PLAYER_NPC_CHAT_COMMAND;
    }

    public static class RequestData {
        private Long npcId;
    }
}
