package com.infinity.common.msg.platform.vote;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class NotifyVoteResponse extends BaseMsg<NotifyVoteResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.VOTE_NOTIFY_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.VOTE_NOTIFY_COMMAND;
    }

    @Data
    public static class ResponseData {

        private int roomId;

        private int state;

        private long endTime;

        private int yesCount;

        private int noCount;

        private String content;

        private String yesContent;

        private String noContent;

    }
}
