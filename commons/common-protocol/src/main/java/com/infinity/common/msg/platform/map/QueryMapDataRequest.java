package com.infinity.common.msg.platform.map;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//查询地图上物品的数据
public class QueryMapDataRequest extends BaseMsg {

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
}
