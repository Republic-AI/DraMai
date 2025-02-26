package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.manager.NFTManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.PlayerNFTRequest;
import com.infinity.common.msg.platform.npc.PlayerNFTResponse;
import com.infinity.common.msg.platform.npc.QueryNFTResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * 玩家操作NFT
 * from:H5客户端
 * target:platform
 */
@Slf4j
public class QueryNFTInfoTask extends BaseTask<PlayerNFTRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_NFT_COMMAND;
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
        QueryNFTResponse response = new QueryNFTResponse();
        response.setPlayerId(playerId);
        QueryNFTResponse.ResponseData data = new QueryNFTResponse.ResponseData();
        response.setData(data);
        data.setNftInfo(NFTManager.getInstance().getNftMap());
        sendMessage(response);
        return false;
    }


}
