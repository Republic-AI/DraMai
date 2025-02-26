package com.infinity.ai.quartz.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class PlatformRunListener implements SpringApplicationRunListener {
    public PlatformRunListener(SpringApplication application, String[] args) {
    }

    //configurableApplicationContext 准备妥当，允许将其调整
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    //ConfigurableApplicationContext 已装载，但仍未启动
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    //ConfigurableApplicationContext 已启动，此时 Spring Bean 已初始化完成
    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("started..............");
        QuartzStarter.getInstance().setAppContext(context);
        QuartzStarter.getInstance().start();
    }

    //Spring 应用正在运行
    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("running..............");
        QuartzStarter.getInstance().run();
    }

    //Spring 应用运行失败
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        exception.printStackTrace();
        System.out.println(exception.getMessage());
        QuartzStarter.getInstance().exit(exception.getMessage());
    }
}
