package com.infinity.common.msg.platform.command;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npcdata.MapItem;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.common.msg.platform.npcdata.WorldData;
import lombok.Data;

import java.util.List;

//NPC指令同步到python
public class NpcCommandSyncResponse extends BaseMsg<NpcCommandSyncResponse.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PYTHON;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_COMMAND_SYNC_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_COMMAND_SYNC_COMMAND;
    }

    @Data
    public static class RequestData {

        private Long npcId;

        private String command;
    }
}
