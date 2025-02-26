package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//twitter授权
public class TwitterCodeRequest extends BaseMsg<TwitterCodeRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.TWITTER_CODE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.TWITTER_CODE_COMMAND;
    }

    @Data
    public static class RequestData {

        private String code;

        private String refreshToken;

        private String state;
    }
}
