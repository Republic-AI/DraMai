package com.infinity.common.msg.chat;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


//查询猫聊天信息
public class QueryChatRequest extends BaseMsg<QueryChatRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_CHAT;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kQueryChatCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kQueryChatCommand;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //NPCID
        private Long npcId;
        //查询页码，默认1
        //private Integer pageNum;
    }
}
