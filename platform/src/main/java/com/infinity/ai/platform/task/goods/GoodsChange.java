package com.infinity.ai.platform.task.goods;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.network.ManagerService;
import com.infinity.task.ITask;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 道具变动通知
 */
@Slf4j
public class GoodsChange {

    private static class GoodsChangeHolder {
        private static final GoodsChange INSTANCE = new GoodsChange();
    }

    public static GoodsChange getInstance() {
        return GoodsChangeHolder.INSTANCE;
    }

    private GoodsChange() {
    }

    /**
     * 物品变动通知
     *
     * @param playerId
     */
    public void newNotifyGoods(long playerId, Collection<Integer> goodsIds) {
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            log.error("GoodsChange newNotifyGoods error, player not online, playerId={}", player);
            return;
        }

        ITask goodsTask = new NotifyGoodsTask(playerId, goodsIds);
        goodsTask.init();
        ManagerService.getTaskManager().add(goodsTask);
    }
}


