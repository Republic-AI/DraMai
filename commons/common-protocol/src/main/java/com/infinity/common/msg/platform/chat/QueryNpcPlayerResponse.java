package com.infinity.common.msg.platform.chat;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class QueryNpcPlayerResponse extends BaseMsg<QueryNpcPlayerResponse.ReponseData> {

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


    @Data
    public static class ReponseData {
        private Map<Long, List<PlayerNpcChatVo>> playerNpcChatDataMap;
    }
}
