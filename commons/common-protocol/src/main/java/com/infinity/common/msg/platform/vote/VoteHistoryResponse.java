package com.infinity.common.msg.platform.vote;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;

public class VoteHistoryResponse extends BaseMsg<VoteHistoryResponse.ResponseData> {

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
    public static class ResponseData {

        private List<VoteHistoryInfo> voteHistoryInfoList;

    }

    @Data
    public static class VoteHistoryInfo {

        private int yesCount;

        private int noCount;

        private int myYesCount;

        private int myNoCount;

        private String content;
    }
}
