package com.infinity.ai.platform.task.live;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.task.npc.PlayerNpcSetTask;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.LeaveLiveRequest;
import com.infinity.common.msg.platform.live.LeaveLiveResponse;
import com.infinity.common.msg.platform.live.SwithLiveRequest;
import com.infinity.common.msg.platform.live.SwithLiveResponse;
import com.infinity.common.msg.platform.npc.NpcData;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 切换NPC房间
 */
@Slf4j
public class LeaveLiveTask extends BaseTask<LeaveLiveRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.LEAVE_LIVE_COMMAND;
    }

    @Override
    public boolean run0() {
        LeaveLiveRequest msg = this.getMsg();
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

        int roomId = player.getRoomId();

        NpcRoom npcRoom = RoomManager.getInstance().getRoomMap().get(roomId);
        if (npcRoom == null) {
            sendErrorMsg(ErrorCode.SystemError, ErrorCode.SystemErrorMessage, msg);
        }

        npcRoom.getPlayerList().remove(playerId);
        player.setRoomId(0);

        LeaveLiveResponse swithLiveResponse = new LeaveLiveResponse();
        swithLiveResponse.setPlayerId(playerId);
        LeaveLiveResponse.ResponseData responseData = new LeaveLiveResponse.ResponseData();
        responseData.setRoomId(roomId);
        swithLiveResponse.setData(responseData);
        sendMessage(swithLiveResponse);
        return true;
    }


}
