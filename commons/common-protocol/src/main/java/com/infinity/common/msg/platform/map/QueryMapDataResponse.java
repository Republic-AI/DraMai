package com.infinity.common.msg.platform.map;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

//查询地图上物品的数据
public class QueryMapDataResponse extends BaseMsg<List<QueryMapDataResponse.ResponseData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_MAP_DATA_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_MAP_DATA_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {
        //物品的ID
        private String objId;
        //物品的状态
        private String state;

        //物品坐标
        private Integer x;
        private Integer y;

        //物品数据，可空
        private Map<String, Object> params;
    }
}
