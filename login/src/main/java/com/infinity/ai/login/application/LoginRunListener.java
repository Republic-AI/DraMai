package com.infinity.ai.login.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class LoginRunListener implements SpringApplicationRunListener {
    public LoginRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("started..............");
        LoginStarter.getInstance().setAppContext(context);
        LoginStarter.getInstance().start();
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("running..............");
        LoginStarter.getInstance().run();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        exception.printStackTrace();
        System.out.println(exception.getMessage());
        LoginStarter.getInstance().exit(exception.getMessage());
    }
}
