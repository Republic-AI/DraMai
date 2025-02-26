package com.infinity.ai.platform.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class PlatformRunListener implements SpringApplicationRunListener {
    public PlatformRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        log.info("===========started..............");
        GameStarter.getInstance().setAppContext(context);
        GameStarter.getInstance().start();
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        log.info("===========running..............");
        GameStarter.getInstance().run();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        exception.printStackTrace();
        System.out.println(exception.getMessage());
        GameStarter.getInstance().exit(exception.getMessage());
    }
}
