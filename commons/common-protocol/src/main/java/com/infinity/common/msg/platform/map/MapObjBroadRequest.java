package com.infinity.common.msg.platform.map;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//服务端推送物品状态变更通知到客户端（广播消息）
public class MapObjBroadRequest extends BaseMsg<MapObjBroadRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.MAP_DATA_BROAD_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.MAP_DATA_BROAD_COMMAND;
    }

    @Data
    public static class RequestData {
        //物品ID
        private String oid;
        //属性数据
        private Integer state;
        //private Map<String, Object> params;
    }
}
