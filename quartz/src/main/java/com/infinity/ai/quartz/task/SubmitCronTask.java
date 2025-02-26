package com.infinity.ai.quartz.task;

import com.infinity.ai.quartz.cron.CronJobTools;
import com.infinity.ai.quartz.cron.CronTask;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.timer.SubmitExpridedMessage;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 提交定时任务
 */
@Slf4j
public class SubmitCronTask extends BaseTask<SubmitExpridedMessage> {

    public SubmitCronTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_CODE_TIMER_SUBMIT;
    }

    @Override
    public boolean run0() {
        SubmitExpridedMessage timerMsg = this.getMsg();
        long cost = (System.currentTimeMillis() - timerMsg.getStart()) * 2;
        log.debug("SubmitCronTask start={},cost={},delay={}", timerMsg.getStart(), cost, timerMsg.getDelay() - cost);
        CronTask ct = new CronTask(timerMsg.getDelay() - cost, timerMsg, timerMsg.getNodeId());
        CronJobTools.addSystemTask(ct);
        return true;
    }
}
