package com.infinity.common.msg.platform.vote;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class OpVoteResponse extends BaseMsg<OpVoteResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.OP_VOTE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.OP_VOTE_COMMAND;
    }

    @Data
    public static class ResponseData {

        private int roomId;

        private boolean choose;

        private int reward;

    }
}
