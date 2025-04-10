package com.infinity.common.msg.platform.room;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

public class QueryRoomListRequest extends BaseMsg<QueryRoomListRequest.RoomListData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_ROOM_LIST_COMMAND;
    }

    public static int getCmd(){
        return ProtocolCommon.QUERY_ROOM_LIST_COMMAND;
    }

    public static class RoomListData {

    }
}
