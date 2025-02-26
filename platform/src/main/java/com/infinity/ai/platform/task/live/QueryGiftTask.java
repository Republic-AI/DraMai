package com.infinity.ai.platform.task.live;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.config.data.GiftCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.GiftData;
import com.infinity.common.msg.platform.live.QueryGiftRequest;
import com.infinity.common.msg.platform.live.QueryGiftResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * 查询礼物数据
 * from: H5
 * target: platform
 */
@Slf4j
public class QueryGiftTask extends BaseTask<QueryGiftRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_GIFT_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryGiftRequest msg = this.getMsg();

        if (playerId <= 0) {
            log.debug("QueryGiftTask playerId is error,request msg={}", msg);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return true;
        }

        sendMessage(buildResponse(player, msg));
        return true;
    }

    private BaseMsg buildResponse(Player player, QueryGiftRequest msg) {
        //组装数据
        QueryGiftResponse response = new QueryGiftResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        List<GiftData> giftData = new ArrayList<>();
        List<GiftCfg> giftCfgs = GameConfigManager.getInstance().getGiftCfgManager().allGiftCfg();
        giftCfgs.stream().forEach(cfg -> {
            GiftData data = GiftData.builder().giftId(cfg.getId())
                    .name(cfg.getName())
                    .icon(cfg.getIcon())
                    .price(cfg.getPrice())
                    .type(cfg.type)
                    .build();
            giftData.add(data);
        });

        response.setData(giftData);
        return response;
    }
}
