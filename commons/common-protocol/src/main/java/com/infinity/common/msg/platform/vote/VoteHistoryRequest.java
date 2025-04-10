package com.infinity.common.msg.platform.vote;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class VoteHistoryRequest extends BaseMsg<VoteHistoryRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.VOTE_HISTORY_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.VOTE_HISTORY_COMMAND;
    }

    @Data
    public static class RequestData {

        private int roomId;

    }
}
