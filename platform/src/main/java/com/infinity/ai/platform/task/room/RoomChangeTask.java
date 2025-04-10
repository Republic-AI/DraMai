package com.infinity.ai.platform.task.room;

import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.live.FurnitureData;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.config.data.FurnitureCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.room.*;
import com.infinity.common.utils.DateUtil;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoomChangeTask extends BaseTask<RoomChangeRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.ROOM_CHANGE_COMMAND;
    }

    @Override
    public boolean run0() {
        if (playerId <= 0) {
            log.debug("RoomChangeTask playerId is error,request msg={}", msg);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return true;
        }

        RoomChangeRequest msg = this.getMsg();

        int furnitrueId = msg.getData().getFurnitureId();

        FurnitureCfg furnitureCfg = GameConfigManager.getInstance().getFurnitureCfgManager().get(furnitrueId);
        if (furnitureCfg == null) {
            log.error("FurnitureCfg not found,request msg={}", msg);
            return false;
        }

        int npcId = furnitureCfg.getNpcId();

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);

        NPC npc = npcHolder.getNpc();

        int roomId = npc.getRoomId();

        NpcRoom npcRoom = RoomManager.getInstance().getRoom(roomId);

        if (npcRoom == null) {
            log.error("NpcRoom not found,request msg={}", msg);
            return false;
        }
        long now = System.currentTimeMillis();
        FurnitureData furnitureData = npcRoom.getFurnitureMap().get(furnitrueId);
        if (furnitureData != null) {
            log.error("furniture in cd,request msg={}", msg);
            return false;
        }
        long time = (long) furnitureCfg.getTime() * DateUtil.OneMinuteMs;
        furnitureData = new FurnitureData();
        furnitureData.setEndTime(now + time);
        furnitureData.setPlayerId(playerId);
        furnitureData.setFurnitureId(furnitrueId);
        npcRoom.getFurnitureMap().put(furnitureCfg.getType(), furnitureData);

        RoomChangeReponse response = new RoomChangeReponse();
        RoomChangeReponse.ReponseData reponseData = new RoomChangeReponse.ReponseData();
        reponseData.setType(furnitureCfg.getType());
        reponseData.setFurnitureMsgData(new FurnitureMsgData(furnitrueId, time));
        response.setData(reponseData);
        BroadcastMesage.getInstance().send(roomId, response.toString());
        Threads.addListener(ThreadConst.TIMER_1S, furnitrueId,"Furniture#remove", new IntervalTimer(time, 1000) {
            @Override
            public boolean exec0(int interval) {
                npcRoom.getFurnitureMap().remove(furnitureCfg.getType());
                RoomChangeReponse response = new RoomChangeReponse();
                RoomChangeReponse.ReponseData reponseData = new RoomChangeReponse.ReponseData();
                reponseData.setType(furnitureCfg.getType());
                reponseData.setFurnitureMsgData(new FurnitureMsgData(furnitrueId, 0));
                response.setData(reponseData);
                BroadcastMesage.getInstance().send(roomId, response.toString());
                return true;
            }
        });
        return true;
    }
}
