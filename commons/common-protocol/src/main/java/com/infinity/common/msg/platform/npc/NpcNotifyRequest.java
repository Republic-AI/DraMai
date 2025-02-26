package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Npc消息推送
public class NpcNotifyRequest extends BaseMsg<NpcNotifyRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NEW_NPC_NOTIFY;
    }

    public static int getCmd() {
        return ProtocolCommon.NEW_NPC_NOTIFY;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //本玩家的NPC
        private NpcData myNpc;
    }
}
