package com.infinity.ai.platform.task.player;

import com.infinity.ai.domain.tables.PlayerNpc;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.LogoutRequest;
import com.infinity.common.msg.platform.npc.NpcOfflineRequest;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 用户退出或者掉线
 */
@Slf4j
public class LogoutTask extends BaseTask<LogoutRequest> {

    public LogoutTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.OFF_LINE_COMMAND;
    }

    @Override
    public boolean run0() {
        LogoutRequest msg = this.getMsg();
        log.debug("player logout,msg={}", msg.toString());

        /*LogoutResponse response = new LogoutResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(msg.getPlayerId());
        sendMessage(msg.getPlayerId(), response);*/

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(msg.getPlayerId());
        if (player != null) {
            PlayerNpc npc = player.getPlayerModel().get_v().getNpc();
            Set<Long> npcIds = npc.getNpcIds();
            if (npcIds != null && npcIds.size() > 0) {
                //广播npc下线消息给其他玩家
                broadcastOfflineMsg(npcIds, player, msg);
                //清除npc数据
                npcIds.stream().forEach(npcId-> NpcManager.getInstance().removeOnlineNpc(npcId));
                Threads.removeListener(ThreadConst.TIMER_1S, npc.getNpcId(), player.getNpcRemoveListenerId());
            }

            player.getPlayerModel().setLastofftime(System.currentTimeMillis());
            PlayerManager.getInstance().removeOnlinePlayer(msg.getPlayerId());
        }

        return true;
    }

    //广播消息给玩家
    private void broadcastOfflineMsg(Set<Long> npcIds, Player player, LogoutRequest msg) {
        NpcOfflineRequest response = new NpcOfflineRequest();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        NpcOfflineRequest.RequestData data = new NpcOfflineRequest.RequestData();
        data.setNpcIds(npcIds);
        response.setData(data);
        BroadcastMesage.getInstance().send(player.getPlayerID(), response.toString());
    }
}
