package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//登出
public class LogoutResponse extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.OFF_LINE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.OFF_LINE_COMMAND;
    }
}
