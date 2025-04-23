package com.infinity.common.msg.platform.room;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class RoomItemChangeResponse extends BaseMsg<RoomItemChangeResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.ROOM_ITEM_CHANGE_COMMAND;
    }

    public static int getCmd(){
        return ProtocolCommon.ROOM_ITEM_CHANGE_COMMAND;
    }

    @Data
    public static class ResponseData {
        private RoomItemData roomItemData;
    }
}
