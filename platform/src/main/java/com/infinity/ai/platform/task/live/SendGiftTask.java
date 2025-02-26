package com.infinity.ai.platform.task.live;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.manager.BagManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.live.Room;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.config.data.GiftCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.GiftCfgManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.RankData;
import com.infinity.common.msg.platform.live.SendGiftRequest;
import com.infinity.common.msg.platform.live.SendGiftResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

/**
 * 送礼
 */
@Slf4j
public class SendGiftTask extends BaseTask<SendGiftRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.SEND_GIFT_COMMAND;
    }

    @Override
    public boolean run0() {
        SendGiftRequest msg = this.getMsg();
        log.debug("SendGiftTask,msg={}", msg.toString());

        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("Send Gift error, playerId params error,playerId={}", playerId);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return false;
        }

        if (msg.getData().getGiftId() <= 0 || msg.getData().getNum() <= 0) {
            log.debug("send gift fail, giftId or num must be greater than zero");
            return false;
        }

        sendGift(player);
        return true;
    }

    private void sendGift(Player player) {
        SendGiftRequest msg = this.getMsg();
        int giftId = msg.getData().getGiftId();
        long num = msg.getData().getNum();

        //礼物配置
        GiftCfgManager manager = GameConfigManager.getInstance().getGiftCfgManager();
        GiftCfg giftCfg = manager.get(giftId);
        if (giftCfg == null) {
            throw new BusinessException(ResultCode.GIFT_CONFIG_ERROR);
        }

        //送礼扣除金币
        long costMoney = num * giftCfg.price;
        //贡献度=玩家在该NPC处购买礼物的花费*10
        long score = costMoney * 10;
        BagManager bag = player.getBag();
        Goods money = bag.getGoodsDefault(GoodsConsts.ITEM_MONEY_ID);
        synchronized (money) {
            if (costMoney > money.count) {
                throw new BusinessException(ResultCode.COINS_NOT_ENOUGH_ERROR);
            }
            money.count -= (int) costMoney;
        }

        //排行榜
        long roomId = msg.getData().getRoomId();
        RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_RANK;
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RScoredSortedSet<Long> roomRank = redissonClient.getScoredSortedSet(liveRoom.getKey(roomId));
        Double totalScore = roomRank.addScore(score, player.getPlayerID());
        Integer rank = roomRank.rank(player.getPlayerID());


        SendGiftResponse.ResponseData data = new SendGiftResponse.ResponseData();
        SendGiftResponse.GiftData gift = SendGiftResponse.GiftData.builder()
                .giftId(giftId).num(num).roomId(roomId).build();
        data.setGift(gift);

        RankData me = RankData.builder()
                .name(player.getName())
                .score(totalScore == null ? 0L : totalScore.longValue())
                .sort(rank + 1)
                .build();
        data.setRank(me);

        //广播礼物给房间的所有人
        Room.getInstance().forEachNotify(roomId, playerId -> {
            SendGiftResponse response = new SendGiftResponse();
            response.setRequestId(msg.getRequestId());
            response.setSessionId(msg.getSessionId());
            response.setPlayerId(playerId);
            response.setData(data);
            MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
            return null;
        });
    }
}
