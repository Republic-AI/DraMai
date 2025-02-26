package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class QueryNFTRequest extends BaseMsg<QueryNFTRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_NFT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_NFT_COMMAND;
    }

    @Data
    public static class RequestData {
    }
}
