package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class NpcChatHistoryRequest extends BaseMsg<NpcChatHistoryRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.CHAT_HISTORY_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.CHAT_HISTORY_COMMAND;
    }

    @Data
    public static class RequestData {

        private int type; // 1 个人发言 2 聊天

        private int page;

        private int pageSize;

        private int roomId;
    }
}
