package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//查询角色信息
public class QueryCharaterRequest extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.CHARATER_QUERY_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.CHARATER_QUERY_COMMAND;
    }
}
