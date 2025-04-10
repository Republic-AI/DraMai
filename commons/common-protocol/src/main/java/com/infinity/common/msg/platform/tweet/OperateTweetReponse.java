package com.infinity.common.msg.platform.tweet;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//切换NPC
public class OperateTweetReponse extends BaseMsg<OperateTweetReponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.COMMENT_TWEET_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.COMMENT_TWEET_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {

        private long tweetId;

        private int type;

        private String content;

        private long replyId;

        private String nickName;

        private long commentId;

        private String userNo;

        private int chooseIndex;

        private List<Integer> rateList;
    }
}
