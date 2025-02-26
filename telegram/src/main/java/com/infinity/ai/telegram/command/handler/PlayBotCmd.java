package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.CmdEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendGame;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//@Component
//@Slf4j
public class PlayBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.INVITE_BTN;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
        context.register(CmdEnum.PLAY_CMD.name(), this);
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        long chatId = update.getMessage().getChatId();
        sendGame(chatId,bot);
    }

    private void sendGame(long chatId, AbsSender bot) {
        SendGame sendGame = new SendGame();
        sendGame.setChatId(chatId);
        sendGame.setGameShortName(catBotConfig.getGameName());

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton playBtn = new InlineKeyboardButton();
        playBtn.setText("Play Cat Academy");

        CallbackGame game = new CallbackGame();
        playBtn.setCallbackGame(game);

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(catBotConfig.getGameUrl());
        playBtn.setWebApp(webAppInfo);
        row1.add(playBtn);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(row1);

        InlineKeyboardMarkup markUp = new InlineKeyboardMarkup(backInlineKeyboardButtonList);
        sendGame.setReplyMarkup(markUp);

        try {
            bot.execute(sendGame);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
