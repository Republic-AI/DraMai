package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

//NPC数据同步到python
public class NpcDataSyncRequest extends BaseMsg {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PYTHON;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_DATA_SYNC_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_DATA_SYNC_COMMAND;
    }
}
