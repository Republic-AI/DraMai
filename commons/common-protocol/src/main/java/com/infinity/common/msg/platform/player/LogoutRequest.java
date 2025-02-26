package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//退出任务
public class LogoutRequest extends BaseMsg<LoginRequest.RequestData> {

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

    @Data
    public static class RequestData {
        //0.断线退出,1.用户主动退出
        private int type;
        private String sourceServiceId;// 哪个服务节点改的
    }
}