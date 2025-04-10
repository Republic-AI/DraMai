package com.infinity.common.msg.platform.tweet;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//切换NPC
public class QueryTweetReponse extends BaseMsg<QueryTweetReponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_TWEETS_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_TWEETS_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {

        private int roomId;

        private List<TweetVo> tweetVoList;


    }
}
