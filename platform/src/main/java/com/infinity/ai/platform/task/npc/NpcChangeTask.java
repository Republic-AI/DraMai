package com.infinity.ai.platform.task.npc;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.chat.PChatNpcRequest;
import com.infinity.common.msg.platform.npc.NpcChangeRequest;
import com.infinity.common.msg.platform.npc.NpcChangeResponse;
import com.infinity.common.msg.platform.npc.NpcReplyChatResponse;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
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
        NPC npc = npcHolder.getNpc();
        PNpc pNpc = npc.getNpcModel();
        pNpc.setDressId(dressId);


        NpcChangeResponse npcChangeResponse = new NpcChangeResponse();
        NpcChangeResponse.ReponseData reponseData = new NpcChangeResponse.ReponseData(dressId, npcId);
        npcChangeResponse.setData(reponseData);
        BroadcastMesage.getInstance().send(playerId, npcChangeResponse.toString());
        return false;
    }
}
