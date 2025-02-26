package com.infinity.ai.platform.event.event;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.event.ActionParams;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.task.npc.PlayerNpcSetTask;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.msg.platform.npc.NpcNotifyRequest;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

//3.登录游戏
@Slf4j
public class LoginAction extends AbstractAction {

    /**
     * taskPrams 格式: []
     *
     * @param params
     */
    @Override
    public void execute(ActionParams params) {
        log.debug("LoginAction execute,params={}", params);
        long playerId = params.getPlayerId();

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);

        //登录广播NPC上线通知给其他玩家
        broadcastNpc(player);
    }

    private void broadcastNpc(Player player) {
        Long npcId = player.getPlayerModel().get_v().getNpc().getNpcId();
        if (npcId != null && npcId > 0) {
            NpcHolder onlineNpcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
            PNpc npcModel = onlineNpcHolder.getNpcModel();
            if (npcModel != null) {
                NpcNotifyRequest.RequestData data = new NpcNotifyRequest.RequestData();
                data.setMyNpc(PlayerNpcSetTask.buildNpcData(npcModel));

                NpcNotifyRequest response = new NpcNotifyRequest();
                response.setRequestId(RequestIDManager.getInstance().RequestID(false));
                response.setPlayerId(player.getPlayerID());
                response.setData(data);
                BroadcastMesage.getInstance().send(player.getPlayerID(), response.toString());
            }
        }
    }

}
