package com.infinity.common.msg.platform.sys;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//首页猫选择
public class HeartRequest extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.SYS_HEART_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.SYS_HEART_COMMAND;
    }

    //心跳信息
    private String msg;
}
