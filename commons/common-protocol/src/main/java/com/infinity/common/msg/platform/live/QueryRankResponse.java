package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;

//排行榜
public class QueryRankResponse extends BaseMsg<QueryRankResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_RANK_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_RANK_COMMAND;
    }

    @Data
    public static class ResponseData {
        //当前玩家排行数据
        private RankData me;
        //排行榜数据
        private List<RankData> rankData;
    }
}
