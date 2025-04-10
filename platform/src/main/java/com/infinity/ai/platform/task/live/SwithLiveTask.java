package com.infinity.ai.platform.task.live;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.live.FurnitureData;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.task.npc.PlayerNpcSetTask;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.SwithLiveRequest;
import com.infinity.common.msg.platform.live.SwithLiveResponse;
import com.infinity.common.msg.platform.npc.NpcData;
import com.infinity.common.msg.platform.room.FurnitureMsgData;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切换NPC房间
 */
@Slf4j
public class SwithLiveTask extends BaseTask<SwithLiveRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.SWITH_LIVE_COMMAND;
    }

    @Override
    public boolean run0() {
        SwithLiveRequest msg = this.getMsg();
        log.debug("SwithLiveTask,msg={}", msg.toString());

        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("Swith Live error, playerId params error,playerId={}", playerId);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return false;
        }

        int roomId = msg.getData().getRoomId();

        NpcRoom npcRoom = RoomManager.getInstance().getRoomMap().get(roomId);
        if (npcRoom == null) {
            sendErrorMsg(ErrorCode.SystemError, ErrorCode.SystemErrorMessage, msg);
        }

        npcRoom.getPlayerList().add(playerId);
        player.setRoomId(roomId);

        SwithLiveResponse swithLiveResponse = new SwithLiveResponse();
        List<NpcData> npcDataList = new ArrayList<>();
        for (long npcId : npcRoom.getNpcList()) {
            NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
            if (npcHolder != null) {
                NPC npc = npcHolder.getNpc();
                PNpc pNpc = npc.getNpcModel();
                NpcData myNpc = PlayerNpcSetTask.buildNpcData(npc, pNpc);
                myNpc.setRequestData(npc.getRequestData());
                npcDataList.add(myNpc);
            }
        }
        SwithLiveResponse.ResponseData responseData = new SwithLiveResponse.ResponseData();
        responseData.setOtherNpc(npcDataList);

        long now = System.currentTimeMillis();
        Map<String, FurnitureMsgData> furnitureMap = new HashMap<>();
        for (Map.Entry<String, FurnitureData> entry : npcRoom.getFurnitureMap().entrySet()) {
            furnitureMap.put(entry.getKey(), new FurnitureMsgData(entry.getValue().getFurnitureId(), (int) (entry.getValue().getEndTime() - now)));
        }
        responseData.setFurnitureMsgDataMap(furnitureMap);

        swithLiveResponse.setData(responseData);
        swithLiveResponse.setPlayerId(playerId);
        sendMessage(swithLiveResponse);
        return true;
    }
}