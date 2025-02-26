package com.infinity.ai.telegram.bot;

import com.infinity.ai.telegram.config.CatBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
public class BotInitializer {

    @Bean
    public CatWebhookBot catWebhookBot(CatBotConfig catBotConfig) {
        //线上使用webhook方式
        try {
            SetWebhook setWebhook = SetWebhook.builder().url(catBotConfig.getWebHookPath()).build();
            CatWebhookBot bot = new CatWebhookBot(catBotConfig, setWebhook);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot, setWebhook);
            return bot;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*@Bean
    public CatLongPollingBot catTelegramBot(CatBotConfig catBotConfig) {
        //测试环境使用polling
        CatLongPollingBot bot = new CatLongPollingBot(catBotConfig);
        try {
            log.info("register telegram bot ........");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("register telegram bot done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bot;
    }*/
}
