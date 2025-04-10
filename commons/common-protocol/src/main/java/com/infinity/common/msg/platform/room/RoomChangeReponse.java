package com.infinity.common.msg.platform.room;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RoomChangeReponse extends BaseMsg<RoomChangeReponse.ReponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.ROOM_CHANGE_COMMAND;
    }

    public static int getCmd(){
        return ProtocolCommon.ROOM_CHANGE_COMMAND;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ReponseData {

        private String type;

        private FurnitureMsgData furnitureMsgData;

    }
}
