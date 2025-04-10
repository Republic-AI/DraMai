package com.infinity.ai.platform.task.room;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.RoomCfgManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.room.QueryRoomListReponse;
import com.infinity.common.msg.platform.room.QueryRoomListRequest;
import com.infinity.common.msg.platform.room.RoomData;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryRoomListTask extends BaseTask<QueryRoomListRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_ROOM_LIST_COMMAND;
    }

    @Override
    public boolean run0() {
        if (playerId <= 0) {
            log.debug("QueryRoomListTask playerId is error,request msg={}", msg);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return true;
        }
        RoomCfgManager roomCfgManager = GameConfigManager.getInstance().getRoomCfgManager();
        List<RoomData> list = new ArrayList<>();
        for (NpcRoom npcRoom : RoomManager.getInstance().getRoomMap().values()) {
            RoomData roomData = new RoomData();
            roomData.setId(npcRoom.getRoomId());
            roomData.setNpcList(npcRoom.getNpcList());
            roomData.setPlayerCount(npcRoom.playerCount());
            roomData.setBannerUrl(roomCfgManager.get(npcRoom.getRoomId()).getBanner());
            list.add(roomData);
        }
        QueryRoomListReponse queryRoomListReponse = new QueryRoomListReponse();
        queryRoomListReponse.setPlayerId(playerId);
        QueryRoomListReponse.RoomListData roomListData = new QueryRoomListReponse.RoomListData();
        roomListData.setRoomDataList(list);
        queryRoomListReponse.setData(roomListData);
        sendMessage(queryRoomListReponse);
        return true;
    }
}
