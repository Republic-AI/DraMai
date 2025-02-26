package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

//查询角色信息
public class QueryCharaterResponse extends BaseMsg<QueryCharaterResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_CHAT;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.CHARATER_QUERY_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.CHARATER_QUERY_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {
        //当前我的npc
        private NpcData myNpc;

        //其他玩家npc
        private List<NpcData> otherNpc;

        private List<NpcData> otherPlayerNpc;

        private Map<Integer, Long> joinIdMap;
    }
}
