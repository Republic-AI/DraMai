package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.CmdEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

//@Component
//@Slf4j
public class InviteBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.INVITE_BTN;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
        context.register(CmdEnum.INVITE_CMD.name(), this);
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        long chatId = update.getMessage().getChatId();
        sendInviteMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/tg-followus.jpg",bot);
    }

    //invite 按钮
    private void sendInviteMessage(long chatId, String photoUrl,AbsSender bot) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83C\uDF89<b>Invite your friends to join our waitlist!</b>\n");
        text.append("      https://www.infinitytest.cc/waitlist");
        text.append("\n");
        text.append("\n");

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text.toString());
        sendPhotoRequest.setParseMode("HTML");


        /*List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton playBtn = new InlineKeyboardButton();
        playBtn.setText("Play for cat");

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(catBotConfig.getGameUrl());
        playBtn.setWebApp(webAppInfo);
        row1.add(playBtn);*/

        /*List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83C\uDF89 Click to invite");
        button.setUrl("https://www.infinitytest.cc/waitlist");
        rowInline.add(button);*/

        /*List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton followBtn = new InlineKeyboardButton();
        followBtn.setText("Follow X");
        followBtn.setUrl("https://t.me/catizenbot");
        rowInline2.add(followBtn);*/

        /*List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);
        backInlineKeyboardButtonList.add(row1);
        backInlineKeyboardButtonList.add(rowInline2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);*/

        try {
            bot.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
