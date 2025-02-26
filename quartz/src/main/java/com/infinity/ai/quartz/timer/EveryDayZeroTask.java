package com.infinity.ai.quartz.timer;

import com.infinity.ai.quartz.cron.CronJobTools;
import com.infinity.ai.quartz.cron.CronTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//每日零点广播
@Component("EveryDayZeroTask")
@Slf4j
public class EveryDayZeroTask implements ITaskRegister {

    @Override
    public boolean isNeedRegister() {
        return false;
    }

    @Override
    public String cron() {
        return "0 0 0 ? * *";
        //return "*/30 * * * * ?";
    }

    @Override
    public void register() {
        log.info("everyDayZero Task register...");
        //相同任务只会注册一次
        CronTask ct = new CronTask(this.jobName());
        CronJobTools.addCronTask(cron(), this.jobName(), ct);
    }

    @Override
    public void execute() {
        log.info("everyDayZero Task execute...");
        /*EveryDayZeroMessage msg = new EveryDayZeroMessage();
        msg.setRequestId(999999999);
        msg.setNotifyType(EveryDayZeroMessage.NotifyType.DAY.getCode());
        msg.setSrc("EveryDayZeroMessage from quartz service.");

        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPlatformService, msg);*/
    }
}
