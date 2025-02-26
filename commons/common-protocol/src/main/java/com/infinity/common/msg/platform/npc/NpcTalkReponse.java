package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class NpcTalkReponse extends BaseMsg<NpcTalkReponse.ReponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PYTHON;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_TALK_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_TALK_COMMAND;
    }

    @Data
    public static class ReponseData {

    }
}
