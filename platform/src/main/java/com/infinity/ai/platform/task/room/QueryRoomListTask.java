package com.infinity.ai.platform.task.room;

import com.infinity.ai.domain.tables.VMap;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.common.config.data.RoomCfg;
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
import java.util.Map;

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
        VMap vMap = MapDataManager.getInstance().getMap().get_v();
        Map<Integer, String> bannerMap = vMap.getBannerConfig();
        List<RoomData> list = new ArrayList<>();
        for (RoomCfg roomCfg : roomCfgManager.getAllJoinCfg()) {
            NpcRoom npcRoom = RoomManager.getInstance().getRoom(roomCfg.getId());
            if (roomCfg.getShow() == 1) {
                RoomData roomData = new RoomData();
                roomData.setId(npcRoom.getRoomId());
                roomData.setNpcList(npcRoom.getNpcList());
                roomData.setPlayerCount(npcRoom.playerCount());
                if (bannerMap.containsKey(roomCfg.getId())) {
                    roomData.setBannerUrl(bannerMap.get(roomCfg.getId()));
                } else {
                    roomData.setBannerUrl(roomCfg.getBanner());
                }
                roomData.setOrder(roomCfg.getOrder());
                list.add(roomData);
            }
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
