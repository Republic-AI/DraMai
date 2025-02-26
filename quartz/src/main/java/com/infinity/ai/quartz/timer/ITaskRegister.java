package com.infinity.ai.quartz.timer;

public interface ITaskRegister {
    //cron表达式
    String cron();

    //任务名称
    default String jobName() {
        return this.getClass().getSimpleName();
    }

    default boolean isNeedRegister() {
        return true;
    }

    //注册任务
    void register();

    //任务执行逻辑
    void execute();
}
