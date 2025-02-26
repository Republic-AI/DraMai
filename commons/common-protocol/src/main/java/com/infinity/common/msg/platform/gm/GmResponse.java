package com.infinity.common.msg.platform.gm;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//GM返回消息
public class GmResponse extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kGmCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kGmCommand;
    }
}
