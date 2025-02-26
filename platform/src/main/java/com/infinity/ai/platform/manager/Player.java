package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.event.ActionService;
import com.infinity.ai.platform.event.ActionTypeEnum;
import com.infinity.ai.PPlayer;
import com.infinity.ai.domain.tables.PlayerTimes;
import com.infinity.ai.platform.event.task.enums.RefurbishTypeEnum;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.utils.DateUtil;
import com.infinity.db.cache.CacheManager;
import com.infinity.db.db.DBManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Player {
    private final long playerId;
    @Getter
    public final BagManager bag;
    @Getter
    private final PlayerTaskDataManager taskDataManager;
    @Getter
    private final SignManager signMgr;

    @Getter
    private Object lock = new Object();

    @Getter
    @Setter
    private long lastTypeToEarnTime;

    @Getter
    @Setter
    private int npcRemoveListenerId;

    /**
     * 创建一个 上线的玩家
     *
     * @param playerID
     * @param playerModel
     */
    public Player(final long playerID, PPlayer playerModel) {
        this(playerID, playerModel, true);
    }

    /**
     * 创建一个 不在线的玩家
     *
     * @param playerID
     */
    public Player(final long playerID) {
        this(playerID, null, false);
    }

    /**
     * 创建一个player,根据online 做不同的初始化
     *
     * @param playerID    玩家id
     * @param playerModel 玩家数据
     * @param online      如果玩家是上线,传true,如果玩家不在线,传false
     */
    private Player(long playerID, PPlayer playerModel, boolean online) {
        playerId = playerID;
        bag = new BagManager(this);
        taskDataManager = new PlayerTaskDataManager(this);
        signMgr = new SignManager(this);
    }

    public boolean online() {
        return PlayerManager.getInstance().getOnlinePlayerWithID(playerId) != null;
    }

    public void dispose() {
        PPlayer player = this.getPlayerModel();
        String name = this.getName();
        DBManager.update(player);
        ActionService.push(playerId, ActionTypeEnum.OFFLINE, player);//离线任务
        log.info("player[{},{}] exit", this.playerId, name);
        CacheManager.get().expire(PPlayer.class, playerId);
        //workMgr.removeCheck();
        GameUserMgr.removeGameUser(playerId, null);
    }

    public void check() {
    }

    public final long getPlayerID() {
        return playerId;
    }

    public final PPlayer getPlayerModel() {
        return DBManager.get(PPlayer.class, playerId);
    }

    public final String getName() {

        return this.getPlayerModel().getName();
    }

    public final String getNickname() {
        return this.getPlayerModel().getNickname();
    }

    /**
     * 每日定时重置/每日首次登录
     * 在登录成功后,最先调用;
     * 每天只会执行一次,或者是重置时,或者是当日首次登录时;
     *
     * @param online true时 定时重置 ; 每日首次登录 false
     */
    public void dailyReset(boolean online) {
        log.debug("player dailyReset run,online={},playerId={}", online, playerId);
        PlayerTimes times = getPlayerModel().get_v().getTimes();

        boolean register = times.getNextResetDay() == 0;
        int nowTime = (int) (System.currentTimeMillis() / 1000);
        long dayReset = times.getNextResetDay();
        long weekReset = times.getNextResetWeek();
        long monthReset = times.getNextResetMonth();

        if (register || dayReset <= nowTime) {
            times.setNextResetDay(DateUtil.nextDay());
            resetDay(online);
        }
        if (register || weekReset <= nowTime) {
            times.setNextResetWeek(DateUtil.nextWeek());
            resetWeek(online);
        }
        if (register || monthReset <= nowTime) {
            times.setNextResetMonth(DateUtil.nextMonth());
            resetMonth(online);
        }
    }

    /**
     * 玩家每日 首次进入游戏时 会调用
     */
    private void resetDay(boolean online) {
        try {
            //每天0点清理日任务
            //taskDataManager.deleteTaskByRefurbish(RefurbishTypeEnum.DAY.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //重置每日体力
            //this.bag.resetAP(online);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 玩家每周 首次进入游戏时 会调用
     */
    private void resetWeek(boolean online) {
        try {
            //每周一早上5点清理周任务
            //taskDataManager.deleteTaskByRefurbish(RefurbishTypeEnum.WEEK.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 玩家每月 首次进入游戏时 会调用
     */
    private void resetMonth(boolean online) {

    }
}



