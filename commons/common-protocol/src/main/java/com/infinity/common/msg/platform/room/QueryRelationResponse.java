package com.infinity.common.msg.platform.room;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;

public class QueryRelationResponse extends BaseMsg<QueryRelationResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_RELATION_COMMAND;
    }

    public static int getCmd(){
        return ProtocolCommon.QUERY_RELATION_COMMAND;
    }

    @Data
    public static class ResponseData {
        private List<RelationData> relationDataList;
    }
}
