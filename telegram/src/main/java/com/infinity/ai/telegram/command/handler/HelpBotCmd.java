package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.command.CmdEnum;
import com.infinity.ai.telegram.config.CatBotConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//@Component
//@Slf4j
public class HelpBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.HELP_BTN;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
        context.register(CmdEnum.HELP_CMD.name(), this);
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        long chatId = update.getMessage().getChatId();
        sendHelpMessage(chatId, bot);
    }

    public void sendHelpMessage(long chatId,AbsSender bot) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83D\uDC31\u200D\uD83E\uDD1D\uD83D\uDC31 Dear Masters, please feel free to contact us anytime while playing our game.\n");
        text.append("\n");

        text.append("\uD83D\uDCE2 <b>Community</b>\n");
        text.append("The Cat_Academia Announcement channel will provide updates and Q&A. Please follow us there.\n");
        text.append("link: https://t.me/InfinityGround_1");
        text.append("\n");
        text.append("\n");

        text.append("\uD83D\uDCEC <b>Report</b>\n");
        text.append("If you encounter any bugs or issues you can't handle, please submit them through the report link.\n");
        text.append("link: <a href='https://qs3bgpj7z30.jp.larksuite.com/share/base/form/shrjpyTOCHRnMavVMzXNDosIuWf'>Cat Academia Bug Problem Report</a>");

        SendMessage message = new SendMessage();
        message.setText(text.toString());
        message.setParseMode("HTML");
        message.disableWebPagePreview();
        message.setChatId(chatId);
        //
        /*List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton playBtn = new InlineKeyboardButton();
        playBtn.setText("Play for cat");

        //CallbackGame game = new CallbackGame();
        //playBtn.setCallbackGame(game);
        //playBtn.setCallbackData("/play");

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(catBotConfig.getGameUrl());
        playBtn.setWebApp(webAppInfo);
        row1.add(playBtn);

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Join community");
        button.setUrl("https://t.me/catizenbot");
        rowInline.add(button);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton followBtn = new InlineKeyboardButton();
        followBtn.setText("Follow X");
        followBtn.setUrl("https://t.me/catizenbot");
        rowInline2.add(followBtn);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(row1);
        backInlineKeyboardButtonList.add(rowInline);
        backInlineKeyboardButtonList.add(rowInline2);

        InlineKeyboardMarkup markUp = new InlineKeyboardMarkup(backInlineKeyboardButtonList);
        message.setReplyMarkup(markUp);*/

        try {
            bot.executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
