package com.infinity.common.msg.platform.sys;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//广播消息
public class BroadcastRequest extends BaseMsg<String> {
    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.MSG_BROADCAST;
    }

    public static int getCmd() {
        return ProtocolCommon.MSG_BROADCAST;
    }
}
