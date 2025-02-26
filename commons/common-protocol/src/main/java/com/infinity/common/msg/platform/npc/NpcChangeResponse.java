package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//聊天
public class NpcChangeResponse extends BaseMsg<NpcChangeResponse.ReponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_CHANGE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_CHANGE_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReponseData {
        //时装ID
        public Integer dressId;
        //npcId
        public Long npcId;
    }
}
