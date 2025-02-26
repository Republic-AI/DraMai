package com.infinity.ai.telegram.command.handler;

import com.infinity.ai.telegram.command.BotCmdContext;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.ai.telegram.command.BotCmd;
import com.infinity.ai.telegram.command.CmdEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//@Component
//@Slf4j
public class StartBotCmd extends BotCmd {

    //@Autowired
    private CatBotConfig catBotConfig;

    @Override
    protected CmdEnum cmdType() {
        return CmdEnum.START_CMD;
    }

    @Override
    protected void afterInit(BotCmdContext context) {
    }

    @Override
    protected void received(Update update, AbsSender bot) {
        long chatId = update.getMessage().getChatId();
        sendStartKeyMessage(chatId, "\uD83D\uDC4F Welcome to Cat Academia!",bot);
        sendStartMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/tg-followus.jpg",bot);
    }

    //start 命令消息
    private void sendStartKeyMessage(long chatId, String text, AbsSender bot) {
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        KeyboardButton playBtn = new KeyboardButton(CmdEnum.PLAY_GAME_BTN.name());
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(catBotConfig.getGameUrl());
        playBtn.setWebApp(webAppInfo);

        KeyboardButton helpBtn = new KeyboardButton(CmdEnum.HELP_BTN.name());
        KeyboardButton followBtn = new KeyboardButton(CmdEnum.FOLLOW_US_BTN.name());
        KeyboardButton inviteBtn = new KeyboardButton(CmdEnum.INVITE_BTN.name());

        row1.add(playBtn);
        row1.add(helpBtn);
        row2.add(followBtn);
        row2.add(inviteBtn);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);

        try {
            bot.executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //start 按钮
    private void sendStartMessage(long chatId, String photoUrl,AbsSender bot) {
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
        InlineKeyboardButton playBtn = new InlineKeyboardButton();
        playBtn.setText(CmdEnum.PLAY_GAME_BTN.name());

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(catBotConfig.getGameUrl());
        playBtn.setWebApp(webAppInfo);
        rowInline.add(playBtn);

        List<InlineKeyboardButton> xInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(CmdEnum.HELP_BTN.name());
        button.setCallbackData(CmdEnum.HELP_BTN.name());
        xInline.add(button);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton followBtn = new InlineKeyboardButton();
        followBtn.setText(CmdEnum.FOLLOW_US_BTN.name());
        followBtn.setCallbackData(CmdEnum.FOLLOW_US_BTN.name());
        rowInline2.add(followBtn);

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        InlineKeyboardButton insButton = new InlineKeyboardButton();
        insButton.setText(CmdEnum.INVITE_BTN.name());
        insButton.setCallbackData(CmdEnum.INVITE_BTN.name());
        rowInline3.add(insButton);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);
        backInlineKeyboardButtonList.add(xInline);
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
