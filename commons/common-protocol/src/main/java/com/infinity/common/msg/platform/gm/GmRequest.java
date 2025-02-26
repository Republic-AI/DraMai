package com.infinity.common.msg.platform.gm;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//GM 命令
public class GmRequest extends BaseMsg<GmRequest.RequestData> {

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //GM 命令
        public String cmd;
    }
}
