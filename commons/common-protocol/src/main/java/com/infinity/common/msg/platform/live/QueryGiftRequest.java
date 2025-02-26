package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//查询直播间礼物列表
public class QueryGiftRequest extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_GIFT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_GIFT_COMMAND;
    }
}
