package com.infinity.common.msg.platform.vote;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class QueryVoteResponse extends BaseMsg<QueryVoteResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_VOTE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_VOTE_COMMAND;
    }

    @Data
    public static class ResponseData {

        private int roomId;

        private int state;

        private long endTime;

        private int yesCount;

        private int noCount;

        private int myYesCount;

        private int myNoCount;

        private String content;

        private String yesContent;

        private String noContent;

    }
}
