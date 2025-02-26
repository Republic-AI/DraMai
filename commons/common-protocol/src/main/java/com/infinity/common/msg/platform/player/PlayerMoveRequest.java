package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class PlayerMoveRequest extends BaseMsg<PlayerMoveRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.PLAYER_MOVE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.PLAYER_MOVE_COMMAND;
    }

    @Data
    public static class RequestData {

        private int gridX;

        private int gridY;
    }
}
