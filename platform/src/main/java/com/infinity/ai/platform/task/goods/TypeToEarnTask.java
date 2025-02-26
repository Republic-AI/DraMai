package com.infinity.ai.platform.task.goods;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.manager.BagManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.live.Room;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.consts.SysParamsConsts;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.*;
import com.infinity.common.msg.platform.live.SendGiftResponse;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 输入信息获取道具
 */
@Slf4j
public class TypeToEarnTask extends BaseTask<TypeToEarnRequest> {

    public TypeToEarnTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.TYPE_TO_EARN_COMMAND;
    }

    @Override
    public boolean run0() {
        TypeToEarnRequest msg = this.getMsg();
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("TypeToEarnTask error, playerId params error,playerId={}", playerId);
            return true;
        }

        //todo 校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return true;
        }

        int interval = GameConfigManager.getInstance().getSysParamCfgManager()
                .getParameterInt(SysParamsConsts.TYPE_TO_EARN_INTERVAL, 8000);
        long now = System.currentTimeMillis();
        if (now - player.getLastTypeToEarnTime() < interval) {
            return true;
        }
        player.setLastTypeToEarnTime(now);
        BagManager bag = player.getBag();
        bag.addGoods(GoodsConsts.ITEM_DOC_ID, 1, false, GoodsSource.TYPE_TO_EARN);
        //bag.addGoods(GoodsConsts.ITEM_MONEY_ID, 1, false, GoodsSource.TYPE_TO_EARN);

        /*Room.getInstance().forEachNotify(msg.getData().getRoomId(), playerIdTemp -> {
            TypeToEarnReponse response = new TypeToEarnReponse();
            response.setRequestId(msg.getRequestId());
            response.setSessionId(msg.getSessionId());
            response.setPlayerId(playerIdTemp);
            //response.setData(data);
            MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
            return null;
        });*/

        TypeToEarnReponse response = new TypeToEarnReponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        //response.setPlayerId(playerId);
        response.setUserNo(player.getPlayerModel().getUserno());
        TypeToEarnReponse.ReponseData data = new TypeToEarnReponse.ReponseData();
        data.setType(GameConsts.TYPE_TO_EARN);
        data.setNpcId(msg.getData().getRoomId());
        response.setData(data);
        //MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
        BroadcastMesage.getInstance().send(playerId, response.toString());
        return false;
    }


}
