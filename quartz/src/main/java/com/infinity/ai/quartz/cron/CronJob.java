package com.infinity.ai.quartz.cron;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

@Slf4j
public class CronJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Object cronTask = jobDataMap.get(CronTask.TASK_KEY_CRON);
        if (cronTask != null && cronTask instanceof CronTask) {
            log.debug("task_key_cron crontask callback");
            ((CronTask) cronTask).end();
        }

        Object usertask = jobDataMap.get(CronTask.TASK_KEY);
        if (usertask != null && usertask instanceof CronTask) {
            log.debug("task_key crontask callback");
            ((CronTask) usertask).end();
        }
    }
}
