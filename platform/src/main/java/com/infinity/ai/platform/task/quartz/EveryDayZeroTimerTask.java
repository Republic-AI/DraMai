package com.infinity.ai.platform.task.quartz;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.timer.EveryDayZeroMessage;
import com.infinity.common.msg.timer.EveryDayZeroMessage.NotifyType;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.ManagerService;
import com.infinity.task.PlayerRebindDummyTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 定时任务quartz服务每天0点会直接发送通知过来，重置服务
 */
@Slf4j
public class EveryDayZeroTimerTask extends BaseTask<EveryDayZeroMessage> {

    public EveryDayZeroTimerTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_CODE_TIMER_EVERYDAYZERO;
    }

    @Override
    public boolean run0() {
        EveryDayZeroMessage msg = this.getMsg();
        log.info("收到定时任务....." + msg.toString());

        // 服务器开启状态,调用 全部在线玩家的 跨日reset;
        if (NotifyType.isDay(msg.notifyType)) {
            dayReset(msg);
        } else if (NotifyType.isWeek(msg.getNotifyType())) {
            weekReset(msg);
        } else if (NotifyType.isMonth(msg.getNotifyType())) {
            monthReset(msg);
        }
        return true;
    }

    //每日重置
    private void dayReset(EveryDayZeroMessage msg) {
        Map<Long, Player> map = PlayerManager.getInstance().getOnlinePlayerMap();
        for (Map.Entry<Long, Player> entry : map.entrySet()) {
            PlayerRebindDummyTask task = new PlayerRebindDummyTask("Player#dailyReset", entry.getKey(), () -> entry.getValue().dailyReset(true));
            ManagerService.getTaskManager().add(task);
        }
    }

    //每周1重置
    private void weekReset(EveryDayZeroMessage msg) {

    }

    //每月1号重置
    private void monthReset(EveryDayZeroMessage msg) {

    }

}
