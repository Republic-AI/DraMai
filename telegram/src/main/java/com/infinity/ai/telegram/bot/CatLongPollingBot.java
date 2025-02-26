package com.infinity.ai.telegram.bot;

import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class CatLongPollingBot extends TelegramLongPollingBot {
    @Autowired
    private BotService botService;
    private CatBotConfig catBotConfig;

    public CatLongPollingBot(CatBotConfig catBotConfig) {
        this.catBotConfig = catBotConfig;
    }

    @Override
    public String getBotUsername() {
        return catBotConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return catBotConfig.getBotToken();
    }

    @Override
    public void onRegister() {
        super.onRegister();

        /*List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand(START_CMD, "Start the bot"));
        commandList.add(new BotCommand(HELP_CMD, "Get help"));
        commandList.add(new BotCommand(FOLLOW_CMD, "Follow us"));
        commandList.add(new BotCommand(INVITE_CMD, "Invite"));

        try {
            SetMyCommands commands = new SetMyCommands();
            commands.setCommands(commandList);
            execute(commands);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onUpdateReceived(Update update) {
        botService.onWebhookUpdateReceived(this, update);
    }

    public void sendTextMessage(long chatId, String text) {
        botService.sendTextMessage(chatId, text, this);
    }

    //绑定钱包
    public void sendBindingMessage(long chatId, String photoUrl) {
        botService.sendBindingMessage(chatId, photoUrl, this);
    }

    //处理点击play game按钮事件
    public String playGameProcess(String tid) {
        return botService.playGameProcess(tid, this);
    }

    //绑定钱包
    public String bindWalletProcess(String tid) {
        return botService.bindWalletProcess(tid, this);
    }
}
