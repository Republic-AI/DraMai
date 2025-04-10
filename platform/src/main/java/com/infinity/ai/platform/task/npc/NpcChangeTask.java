package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.config.data.DressCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcChangeRequest;
import com.infinity.common.msg.platform.npc.NpcChangeResponse;
import com.infinity.common.utils.DateUtil;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NpcChangeTask extends BaseTask<NpcChangeRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.NPC_CHANGE_COMMAND;
    }

    @Override
    public boolean run0() {

        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("SignTask error, playerId params error,playerId={}", playerId);
            return false;
        }

        //todo 校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return false;
        }
        NpcChangeRequest msg = this.getMsg();
        long npcId = msg.getData().getNpcId();
        int dressId = msg.getData().getDressId();

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
        if (npcHolder == null) {
            return false;
        }
        DressCfg dressCfg = GameConfigManager.getInstance().getDressCfgManager().get(dressId);
        long now = System.currentTimeMillis();
        long time = (long) dressCfg.getTime() * DateUtil.OneMinuteMs;
        NPC npc = npcHolder.getNpc();
        npc.setDressId(dressId);
        npc.setDressEndTime(now + time);
        NpcChangeResponse npcChangeResponse = new NpcChangeResponse();
        NpcChangeResponse.ReponseData reponseData = new NpcChangeResponse.ReponseData(dressId, npcId, time);
        npcChangeResponse.setData(reponseData);
        BroadcastMesage.getInstance().send(playerId, npcChangeResponse.toString());
        Threads.addListener(ThreadConst.TIMER_1S, dressId,"Dress#remove", new IntervalTimer(time, 1000) {
            @Override
            public boolean exec0(int interval) {
                npc.setDressId(0);
                npc.setDressEndTime(0);
                NpcChangeResponse npcChangeResponse = new NpcChangeResponse();
                NpcChangeResponse.ReponseData reponseData = new NpcChangeResponse.ReponseData(0, npcId, 0);
                npcChangeResponse.setData(reponseData);
                BroadcastMesage.getInstance().send(npcId, npcChangeResponse.toString());
                return true;
            }
        });
        return false;
    }
}
