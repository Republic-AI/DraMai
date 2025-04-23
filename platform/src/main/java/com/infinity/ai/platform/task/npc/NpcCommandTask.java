package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.consts.SysParamsConsts;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcCommandResponse;
import com.infinity.common.msg.platform.npc.NpcCommandRequest;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NpcCommandTask extends BaseTask<NpcCommandRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.NPC_COMMAND_COMMAND;
    }

    @Override
    public boolean run0() {
        NpcCommandRequest msg = this.getMsg();
        if (msg.getPlayerId() <= 0) {
            log.debug("NpcCommandTask playerId is error,request msg={}", msg);
            return false;
        }
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return false;
        }
        if (msg.getData().getNpcId() <= 0) {
            log.debug("NpcCommandTask npcId is error,request msg={}", msg);
            return false;
        }
        if (msg.getData().getReward() < 0) {
            log.debug("NpcCommandTask reward is error,request msg={}", msg);
            return false;
        }
        int interval = GameConfigManager.getInstance().getSysParamCfgManager()
                .getParameterInt(SysParamsConsts.TYPE_TO_EARN_INTERVAL, 8000);
        long now = System.currentTimeMillis();
        if (now - player.getLastNpcCommandTime() < interval) {
            return false;
        }
        player.setLastNpcCommandTime(now);
        int reward = msg.getData().getReward();
        if (reward == 0) {
            reward = 1;
        }
        player.getBag().addGoods(GoodsConsts.ITEM_MONEY_ID, -reward, false, GoodsSource.TYPE_TO_EARN);

        long npcId = msg.getData().getNpcId();

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
        if (npcHolder == null) {
            return false;
        }
        NPC npc = npcHolder.getNpc();

        npc.addCommand(msg.getData().getContent());

        NpcCommandResponse npcCommandReponse = new NpcCommandResponse();
        NpcCommandResponse.ResponseData responseData = new NpcCommandResponse.ResponseData();
        responseData.setNpcId(msg.getData().getNpcId());
        responseData.setContent(msg.getData().getContent());
        responseData.setReward(msg.getData().getReward());
        responseData.setAvatar(player.getPlayerModel().get_v().getBase().getAvatar());
        responseData.setUserNo(player.getPlayerModel().getUserno());
        responseData.setCdTime(interval);
        npcCommandReponse.setData(responseData);

        for (long playerId : RoomManager.getInstance().getRoom(npc.getRoomId()).getPlayerList()) {
            npcCommandReponse.setPlayerId(playerId);
            GameUser gameUser = GameUserMgr.getGameUser(playerId);
            log.debug("send msg -> {},{}", gameUser, npcCommandReponse);
            if (gameUser != null) {
                MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), npcCommandReponse);
            }
        }
        return false;
    }


}
