package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;


//NPC数据同步到python
public class NpcChatHistoryResponse extends BaseMsg<NpcChatHistoryResponse.ResponseData> {

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
    public static class ResponseData {
        private int type; // 1 个人发言 2 聊天

        private int page;

        private int pageSize;

        private List<NpcSpeakVo> speakDataList;

        private List<NpcChatVo> chatDataList;

        private long gameTime;
    }
}
