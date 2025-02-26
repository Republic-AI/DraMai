package com.infinity.ai.platform.task.sign;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.SignRequest;
import com.infinity.common.msg.platform.player.SignResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 签到
 */
@Slf4j
public class SignTask extends BaseTask<SignRequest> {

    public SignTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.kSignCommand;
    }

    @Override
    public boolean run0() {
        SignRequest msg = this.getMsg();
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

        int sign = player.getSignMgr().sign(msg);
        if (sign == -1) {
            log.info("operation failed:repeated sign,playerId={}", player.getPlayerID());
            return false;
        }

        return true;
    }

    //查询猫数据
    private BaseMsg buildResponse(Player player, SignRequest msg) {
        SignResponse response = new SignResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        SignResponse.ResponseData data = new SignResponse.ResponseData();
        data.setSign(player.getSignMgr().isCanSign() ? 1 : 0);
        data.setItemType(1);
        data.setItemValue(0);
        response.setData(data);
        return response;
    }

}
