package com.infinity.ai.platform.task.goods;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.GoodsData;
import com.infinity.common.msg.platform.goods.NotifyGoodsRequest;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.RequestIDManager;
import com.infinity.task.IRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 道具变动通知
 */
@Slf4j
public class NotifyGoodsTask extends BaseTask implements IRequest {
    private long playerId;
    private Collection<Integer> goodsIds;
    private long requestId;

    public NotifyGoodsTask(final long playerId, Collection<Integer> goodsIds) {
        this.playerId = playerId;
        this.goodsIds = goodsIds;
    }

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public long getRequestID() {
        return requestId;
    }

    @Override
    public void init() {
        this.requestId = RequestIDManager.getInstance().RequestID(false);
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.kNotifyGoodsCommand;
    }

    @Override
    public boolean run0() {
        List<GoodsData> data = this.getData();
        if (data == null || data.size() == 0) {
            log.debug("NotifyGoodsTask break, goods not found, goodsIds={}", goodsIds);
            return true;
        }

        NotifyGoodsRequest response = new NotifyGoodsRequest();
        response.setRequestId(requestId);
        response.setSessionId(null);
        response.setPlayerId(playerId);
        response.setData(data);

        this.sendMessage(playerId, response);
        return true;
    }

    public List<GoodsData> getData() {
        if (this.goodsIds == null || goodsIds.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<GoodsData> dataList = new ArrayList<>();
        ItemBaseDataManager itemManager = GameConfigManager.getInstance().getItemBaseDataManager();
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        List<Goods> goodsList = player.bag.getGoodsList(goodsIds);
        goodsList.forEach(goods -> {
            ItemCfg itemCfg = itemManager.getItemConfigWithID(goods.getGoodsId());
            if(itemCfg != null) {
                GoodsData data = new GoodsData();
                data.setGoodsId(goods.getGoodsId());
                data.setPlayerId(goods.getPlayerId());
                data.setGoodsType(goods.getGoodsType());
                data.setCount(goods.getCount());
                data.setMaxLimit(goods.getMaxLimit());
                dataList.add(data);
            }
        });

        return dataList;
    }
}
