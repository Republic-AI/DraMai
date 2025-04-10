package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class NpcCommandRequest extends BaseMsg<NpcCommandRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_COMMAND_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_COMMAND_COMMAND;
    }

    @Data
    public static class RequestData {

        private Long npcId;

        private String content;

        private Integer reward;
    }
}
