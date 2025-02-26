package com.infinity.ai.telegram.controller;

import com.infinity.ai.telegram.bot.CatWebhookBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
public class WebhookController {
    @Autowired
    private CatWebhookBot catWebhookBot;

    @PostMapping("/callback")
    public void onUpdateReceived(@RequestBody Update update) {
        log.info("收到机器人的请求.......{}", update.toString());
        catWebhookBot.onWebhookUpdateReceived(update);
    }

    //测试部署用
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
