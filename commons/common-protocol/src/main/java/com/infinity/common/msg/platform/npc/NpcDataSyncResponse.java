package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npcdata.MapItem;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.common.msg.platform.npcdata.WorldData;
import lombok.Data;

import java.util.List;

//NPC数据同步到python
public class NpcDataSyncResponse extends BaseMsg<NpcDataSyncResponse.RequestData> {

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

    @Data
    public static class RequestData {
        //游戏世界数据，公共数据
        public WorldData world;
        //地图物品数据
        public List<MapItem> mapObj;
        //NPC 相关数据
        public List<NpcData> npcs;
    }
}
