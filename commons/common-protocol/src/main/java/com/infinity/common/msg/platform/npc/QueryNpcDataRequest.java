package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//python程序查询所有NPC数据
public class QueryNpcDataRequest extends BaseMsg<QueryNpcDataRequest.RequestData> {

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
    public static class RequestData {
        //NPC ID，非空,npcId <= 0代表全局数据，npcId > 0代表npc本身数据
        private Long npcId;
        //clearData
        private boolean init;
    }
}
