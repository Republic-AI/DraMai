package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.CmdEnum;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//@Component
//@Slf4j
public class AnswerBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.INVITE_BTN;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
        //context.register(CmdEnum.PLAY_CMD.name(), this);
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        answerCallbackQuery(update.getCallbackQuery().getId(), catBotConfig.getGameUrl(),bot);
    }

    private void answerCallbackQuery(String callbackQueryId, String url, AbsSender bot) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setUrl(url);

        try {
            bot.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
