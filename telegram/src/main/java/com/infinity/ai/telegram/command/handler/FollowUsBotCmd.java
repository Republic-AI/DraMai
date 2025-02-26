package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.CmdEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

//@Component
//@Slf4j
public class FollowUsBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.FOLLOW_US_BTN;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
        context.register(CmdEnum.FOLLOW_US_CMD.name(), this);
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        long chatId = update.getMessage().getChatId();
        sendFollowMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/tg-followus.jpg",bot);
    }

    //follow us 按钮
    private void sendFollowMessage(long chatId, String photoUrl,AbsSender bot) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83C\uDF81 Follow us and get more fun!\n");
        text.append("\n");
        text.append("\n");

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text.toString());
        sendPhotoRequest.setParseMode("HTML");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDC49 Follow X");
        button.setUrl("https://x.com/Cat_Academia_ai");
        rowInline.add(button);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton followBtn = new InlineKeyboardButton();
        followBtn.setText("\uD83D\uDC49 Follow TikTok");
        followBtn.setUrl("https://www.tiktok.com/@cat_academia");
        rowInline2.add(followBtn);

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        InlineKeyboardButton insButton = new InlineKeyboardButton();
        insButton.setText("\uD83D\uDC49 Follow Ins   ");
        insButton.setUrl("https://www.instagram.com/cat.academia");
        rowInline3.add(insButton);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);
        backInlineKeyboardButtonList.add(rowInline2);
        backInlineKeyboardButtonList.add(rowInline3);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
