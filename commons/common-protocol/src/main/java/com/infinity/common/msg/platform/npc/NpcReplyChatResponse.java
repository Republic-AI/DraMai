package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import com.infinity.common.msg.platform.npcdata.MapItem;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.common.msg.platform.npcdata.WorldData;
import lombok.Data;

import java.util.List;

//NPC数据同步到python
public class NpcReplyChatResponse extends BaseMsg<NpcReplyChatResponse.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PYTHON;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_REPLY_CHAT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_REPLY_CHAT_COMMAND;
    }

    @Data
    public static class RequestData {

        private Long npcId;

        private ChatData chatData;
    }
}
