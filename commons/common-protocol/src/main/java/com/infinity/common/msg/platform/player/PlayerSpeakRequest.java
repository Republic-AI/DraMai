package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class PlayerSpeakRequest extends BaseMsg<PlayerSpeakRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.PLAYER_SPEAK_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.PLAYER_SPEAK_COMMAND;
    }

    @Data
    public static class RequestData {

        private String content;

    }
}
