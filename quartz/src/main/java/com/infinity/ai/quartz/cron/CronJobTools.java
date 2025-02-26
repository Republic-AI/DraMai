package com.infinity.ai.quartz.cron;

import com.infinity.ai.quartz.manager.CronTaskManager;

public class CronJobTools {
    /***
     * 用户相关倒计时
     *
     * @param userId
     * @param cronType
     * @param delay
     * @param task
     */
    public static void addCronTask(long userId, int cronType, long delay, CronTask task) {
        task.setCronType(CronTypeCost.PREFIX_USER_CRON_JOG);
        CronTaskManager.getInstance().addCountDownJob(
                CronTypeCost.PREFIX_USER_CRON_JOG + userId + "_" + task.getId(),
                CronTypeCost.PREFIX_USER_CRON_JOG + userId,
                task);
    }

    /***
     * 系统任务
     *
     * @param task
     */
    public static void addSystemTask(CronTask task) {
        task.setCronType(CronTypeCost.PREFIX_SYS_CRON_JOG);
        CronTaskManager.getInstance().addCountDownJob(
                CronTypeCost.PREFIX_SYS_CRON_JOG + "_" + task.getId(),
                CronTypeCost.PREFIX_SYS_CRON_JOG, task);
        /*
         * CronTaskManager.getInstance().addCountDownJob(CronTypeCost.
         * PREFIX_SYS_LOOP_CRON_JOG + "_" + handler.getId(),
         * CronTypeCost.PREFIX_SYS_LOOP_CRON_JOG, handler, reg);
         */
    }


    public static boolean addCronTask(String cron, String jobName, CronTask task) {
        task.setCronType(CronTypeCost.PREFIX_USER_CRON_JOG);
        String groupName = CronTypeCost.PREFIX_USER_CRON_JOG + "fix";
        return CronTaskManager.getInstance().createJob(jobName, groupName, cron, task, false);
    }

    public static boolean addCronTask(String cron, String jobName, CronTask task,boolean isRemove) {
        task.setCronType(CronTypeCost.PREFIX_USER_CRON_JOG);
        String groupName = CronTypeCost.PREFIX_USER_CRON_JOG + "fix";
        return CronTaskManager.getInstance().createJob(jobName, groupName, cron, task, isRemove);
    }
}
