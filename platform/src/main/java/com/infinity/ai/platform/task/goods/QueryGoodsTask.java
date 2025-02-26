package com.infinity.ai.platform.task.goods;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.manager.BagManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.GoodsData;
import com.infinity.common.msg.platform.goods.QueryGoodsRequest;
import com.infinity.common.msg.platform.goods.QueryGoodsResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 查询背包道具信息
 */
@Slf4j
public class QueryGoodsTask extends BaseTask<QueryGoodsRequest> {

    public QueryGoodsTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.kGoodsSelectCommand;
    }

    @Override
    public boolean run0() {
        QueryGoodsRequest msg = this.getMsg();
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("QueryGoodsTask error, playerId params error,playerId={}", playerId);
            return true;
        }

        //todo 校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return true;
        }
        BagManager bag = player.getBag();
        //bag.addGoods(GoodsConsts.ITEM_DOC_ID, 1, false, GoodsSource.GM);
        //bag.addGoods(GoodsConsts.ITEM_MONEY_ID, 1, false, GoodsSource.GM);

        sendMessage(buildResponse(player, msg));
        return false;
    }

    //查询猫数据
    private BaseMsg buildResponse(Player player, QueryGoodsRequest msg) {
        QueryGoodsResponse response = new QueryGoodsResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        List<GoodsData> dataList = new ArrayList<>();
        Set<Integer> goodsIds = msg.getData() == null ? null : msg.getData().getIds();
        List<Goods> goodsList = (goodsIds == null || goodsIds.size() == 0) ? player.bag.getAll() : player.bag.getGoodsList(goodsIds);
        ItemBaseDataManager itemManager = GameConfigManager.getInstance().getItemBaseDataManager();
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

        response.setData(dataList);
        return response;
    }

}
