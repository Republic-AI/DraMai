package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

//玩家离线，广播NPC消息
public class NpcOfflineRequest extends BaseMsg<NpcOfflineRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_OFFLINE_NOTIFY;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_OFFLINE_NOTIFY;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //npcId 列表
        private Set<Long> npcIds;
    }
}
