package com.infinity.common.msg.platform.goods;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.Set;

//查询道具
public class QueryGoodsRequest extends BaseMsg<QueryGoodsRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kGoodsSelectCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kGoodsSelectCommand;
    }

    @Data
    public static class RequestData {
        //玩家ID
        private Long playerId;

        private Set<Integer> ids;
    }
}
