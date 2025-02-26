package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.List;
import java.util.Map;

//python程序查询所有NPC数据
public class QueryNpcDataResponse extends BaseMsg<QueryNpcDataResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PYTHON;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_NPC_DATA_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_NPC_DATA_COMMAND;
    }

    @Data
    public static class ResponseData {
        //属性数据，可空
        private Map<String, Object> data;

        //其他玩家npc
        private List<NpcData> otherNpc;

        private List<Object> list;
    }
}
