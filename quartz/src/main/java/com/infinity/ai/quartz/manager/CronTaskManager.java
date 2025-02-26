package com.infinity.ai.quartz.manager;

import com.infinity.ai.quartz.cron.CronJob;
import com.infinity.ai.quartz.cron.CronTask;
import com.infinity.ai.quartz.timer.TaskRegister;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

@Slf4j
public class CronTaskManager {
    private static final String TriggerKeyProfix = "T_";
    private static final String DEFAULT_GROUP_NAME = "default_group";
    private static SchedulerFactory gSchedulerFactory;

    public CronTaskManager() {
    }

    private static class Holder {
        private static final CronTaskManager kInstance = new CronTaskManager();
    }

    public static CronTaskManager getInstance() {
        return Holder.kInstance;
    }

    public void init() {
        try {
            gSchedulerFactory = new StdSchedulerFactory("config/quartz.properties");
            startJobs();

            TaskRegister taskRunner = new TaskRegister();
            taskRunner.run();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        if (gSchedulerFactory != null) {
            try {
                Scheduler sched = gSchedulerFactory.getScheduler();
                sched.start();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭所有定时任务
     */
    public void shutdownJobs() {
        log.info("shutdownJobs");
        if (gSchedulerFactory != null) {
            try {
                Scheduler sched = gSchedulerFactory.getScheduler();
                if (!sched.isShutdown()) {
                    sched.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    //true:存在, false:不存在
    public boolean exists(String jobName, String groupName) {
        try {
            Scheduler scheduler0 = gSchedulerFactory.getScheduler();
            JobDetail jd = scheduler0.getJobDetail(new JobKey(jobName, groupName));
            return (jd != null);
        } catch (SchedulerException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public boolean removeJob(String jobName, String groupName) {
        try {
            Scheduler scheduler0 = gSchedulerFactory.getScheduler();
            JobDetail jd = scheduler0.getJobDetail(new JobKey(jobName, groupName));
            if (jd != null) {
                //停止触发器
                TriggerKey tk = new TriggerKey(TriggerKeyProfix + jobName, TriggerKeyProfix + groupName);
                scheduler0.pauseTrigger(tk);
                //移除触发器
                scheduler0.unscheduleJob(tk);
                //删除任务
                scheduler0.deleteJob(jd.getKey());
            }
        } catch (SchedulerException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    //根据cron表达式创建任务，存在则删除
    public boolean createJob(String jobName, String groupName, String cronExpression, CronTask task, boolean isRemove) {
        String gName = groupName;
        if (groupName == null) {
            gName = DEFAULT_GROUP_NAME;
        }

        if (isRemove && !removeJob(jobName, groupName)) {
            return false;
        }

        if (!isRemove && exists(jobName, groupName)) {
            return true;
        }

        try {
            //创建一个Scheduler
            Scheduler scheduler = gSchedulerFactory.getScheduler();

            //创建JobDetail
            JobDetail jobDetail = JobBuilder
                    .newJob(CronJob.class)
                    .withIdentity(jobName, gName)
                    .build();
            jobDetail.getJobDataMap().put(CronTask.TASK_KEY, task);

            // 创建scheduler，调度器, 策略采用错过之后立即执行一次
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, gName)
                    .startNow()
                    .withSchedule(scheduleBuilder)
                    .build();

            //将Job和Trigger添加到Scheduler中
            scheduler.scheduleJob(jobDetail, trigger);
            task.setDetail(this, jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /***
     * 添加一个定时任务
     *
     * @param jobName 任务名
     * @param task 任务执行类
     * @param groupName
     */
    public boolean addCountDownJob(String jobName, String groupName, CronTask task) {
        String gName = groupName;
        if (groupName == null) {
            gName = DEFAULT_GROUP_NAME;
        }

        if (!removeJob(jobName, groupName)) {
            return false;
        }

        try {
            //创建一个Scheduler
            Scheduler scheduler = gSchedulerFactory.getScheduler();

            //创建JobDetail
            JobDetail jobDetail = JobBuilder
                    .newJob(CronJob.class)
                    .withIdentity(jobName, gName)
                    .build();
            jobDetail.getJobDataMap().put(CronTask.TASK_KEY, task);

            //创建一次性触发器，设置执行时间
            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder
                    .newTrigger()
                    .withIdentity(TriggerKeyProfix + jobName, TriggerKeyProfix + gName)
                    .startAt(new Date(task.getEndTime()))
                    .build();

            //将Job和Trigger添加到Scheduler中
            scheduler.scheduleJob(jobDetail, trigger);
            task.setDetail(this, jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean addCountDownJob(String jobName, String groupName, CronTask task, String reg) {
        try {
            Scheduler scheduler0 = gSchedulerFactory.getScheduler();
            JobDetail jd = scheduler0.getJobDetail(new JobKey(jobName, groupName));

            if (jd != null) {
                /** 停止触发器 */
                TriggerKey tk = new TriggerKey(TriggerKeyProfix + jobName, TriggerKeyProfix + groupName);
                scheduler0.pauseTrigger(tk);

                /** 移除触发器 **/
                scheduler0.unscheduleJob(tk);
                /** 删除任务 **/
                scheduler0.deleteJob(jd.getKey());
            }
        } catch (SchedulerException e1) {
            e1.printStackTrace();
            return false;
        }

        try {
            Scheduler scheduler = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(CronJob.class).withIdentity(jobName, groupName).build();
            jobDetail.getJobDataMap().put(CronTask.TASK_KEY_CRON, task);
            CronTrigger trigger = null;
            trigger = TriggerBuilder.newTrigger().withIdentity(TriggerKeyProfix + jobName, TriggerKeyProfix + groupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(reg)).build();
            scheduler.scheduleJob(jobDetail, trigger);
            task.setDetail(this, jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
