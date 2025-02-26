package com.infinity.ai.telegram.service;

import com.infinity.ai.telegram.bot.BotUser;
import com.infinity.ai.telegram.common.Constant;
import com.infinity.ai.telegram.config.CatBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendGame;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
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

@Service
@Slf4j
public class BotService {
    @Autowired
    private CatBotConfig catBotConfig;
    @Autowired
    private UserService userService;

    private final String PLAY_BTN_TXT = "\uD83D\uDD79 Play game";
    private final String FOLLOW_US_BTN_TXT = "✨ Follow us";
    private final String HELP_BTN_TXT = "\uD83D\uDCD6 Help";
    private final String FEEDBACK_BTN_TXT = "\uD83C\uDF81 Feedback";
    private final String BIND_WALLET_BTN_TXT = "\uD83D\uDC5D Bind Wallet";

    private final String START_CMD = "/start";
    private final String HELP_CMD = "/help";
    private final String FOLLOW_CMD = "/follow us";
    private final String INVITE_CMD = "/invite";
    private final String PLAY_CMD = "/play";

    public BotApiMethod<?> onWebhookUpdateReceived(AbsSender absSender, Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info("chatId={}, update={}", chatId, update.toString());
            //chatIds.add(chatId);

            if (messageText.equals(START_CMD) || messageText.startsWith(START_CMD)) {
                User user = update.getMessage().getFrom();
                String name = user.getFirstName() + user.getLastName();
                userService.handleStart(chatId, messageText, name);
                sendStartKeyMessage(chatId, "\uD83D\uDC4F Welcome to Cat Academia!", update, absSender);
                sendStartMessage(chatId, Constant.WELCOME_IMG, update, absSender);
            } else if (messageText.equals(PLAY_CMD)) {
                sendGame(chatId, absSender);
            } else if (messageText.equals(HELP_CMD)) {
                sendHelpMessage(chatId, absSender);
            } else if (messageText.equals(HELP_BTN_TXT)) {
                sendHelpMessage(chatId, absSender);
            } else if (messageText.equals(FOLLOW_US_BTN_TXT)) {
                sendFollowMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/followus.jpg", absSender);
            } else if (messageText.equals(FOLLOW_CMD)) {
                sendFollowMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/followus.jpg", absSender);
            } else if (messageText.equals(FEEDBACK_BTN_TXT)) {
                sendInviteMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/facebook.jpg", absSender);
            } else if (messageText.equals(INVITE_CMD)) {
                sendInviteMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/facebook.jpg", absSender);
            } else if (messageText.equals(BIND_WALLET_BTN_TXT)) {
                sendBindingBtnMessage(chatId, Constant.WALLET_IMG, absSender);
            }
        } else if (update.hasCallbackQuery()) {
            String gameShortName = update.getCallbackQuery().getGameShortName();
            if (catBotConfig.getGameName().equals(gameShortName)) {
                answerCallbackQuery(update.getCallbackQuery().getId(), catBotConfig.getGameUrl(), absSender);
            } else if (PLAY_CMD.equals(update.getCallbackQuery().getData())) {
                answerCallbackQuery(update.getCallbackQuery().getId(), catBotConfig.getGameUrl(), absSender);
            } else if (HELP_BTN_TXT.equals(update.getCallbackQuery().getData())) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                sendHelpMessage(chatId, absSender);
            } else if (FOLLOW_US_BTN_TXT.equals(update.getCallbackQuery().getData())) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                sendFollowMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/followus.jpg", absSender);
            } else if (FEEDBACK_BTN_TXT.equals(update.getCallbackQuery().getData())) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                sendInviteMessage(chatId, "https://catoss.s3.ap-southeast-1.amazonaws.com/images/telegram/whitelist.jpg", absSender);
            } else if (START_CMD.equals(update.getCallbackQuery().getData())) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                sendStartKeyMessage(chatId, "\uD83D\uDC4F Congratulations, the Bot update was successful!", update, absSender);
            }
        }
        return null;
    }

    public String getPlayBtnUrl(Long chatId) {
        BotUser botUser = userService.getBotUser(chatId);
        String url = catBotConfig.getTgUrl() + "play/%s";
        String path = String.format(url, botUser.encryptUserId());
        log.debug("name={},userId={},path={}", botUser.getName(), botUser.getUserId(), path);
        return path;
    }

    public String getPlayBtnUrl(Update update) {
        return getPlayBtnUrl(update.getMessage().getFrom().getId());
    }

    /**
     * 获取登录URL 和推送绑定消息
     *
     * @param tid TG ID, 格式：TG + UserId
     * @return 登录url
     */
    public String playGameProcess(String tid, AbsSender absSender) {
        return "";
    }

    //start 命令消息
    private void sendStartKeyMessage(long chatId, String text, Update update, AbsSender absSender) {
        sendStartKeyMessage(chatId, text, absSender);
    }

    private void sendStartKeyMessage(long chatId, String text, AbsSender absSender) {
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        KeyboardButton playBtn = playGameKeyboardBtn(PLAY_BTN_TXT, getPlayBtnUrl(chatId));
        KeyboardButton helpBtn = new KeyboardButton(HELP_BTN_TXT);
        KeyboardButton followBtn = new KeyboardButton(FOLLOW_US_BTN_TXT);
        //KeyboardButton inviteBtn = new KeyboardButton(INVITE_BTN_TXT);

        row1.add(playBtn);
        //row1.add(inviteBtn);
        row2.add(followBtn);
        row2.add(helpBtn);

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
            absSender.executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private KeyboardButton playGameKeyboardBtn(String btnTxt, String url) {
        KeyboardButton playBtn = new KeyboardButton(btnTxt);
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(url);
        playBtn.setWebApp(webAppInfo);
        return playBtn;
    }

    //start 按钮
    private void sendStartMessage(long chatId, String photoUrl, Update update, AbsSender absSender) {
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
        playBtn.setText(PLAY_BTN_TXT);

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(getPlayBtnUrl(update));
        playBtn.setWebApp(webAppInfo);
        rowInline.add(playBtn);

        List<InlineKeyboardButton> xInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(HELP_BTN_TXT);
        button.setCallbackData(HELP_BTN_TXT);
        xInline.add(button);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton followBtn = new InlineKeyboardButton();
        followBtn.setText(FOLLOW_US_BTN_TXT);
        followBtn.setCallbackData(FOLLOW_US_BTN_TXT);
        rowInline2.add(followBtn);

        /*List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        InlineKeyboardButton insButton = new InlineKeyboardButton();
        insButton.setText(INVITE_BTN_TXT);
        insButton.setCallbackData(INVITE_BTN_TXT);
        rowInline3.add(insButton);*/

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);
        //backInlineKeyboardButtonList.add(rowInline3);
        backInlineKeyboardButtonList.add(rowInline2);
        backInlineKeyboardButtonList.add(xInline);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);

        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHelpMessage(long chatId, AbsSender absSender) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83D\uDC31\u200D\uD83E\uDD1D\uD83D\uDC31 Dear Purrfessors, please feel free to contact us anytime while playing our game.\n");
        text.append("\n");

        text.append("\uD83D\uDCE2 <b>Community</b>\n");
        text.append("Share your gaming experiences and get Q&A support in the Infinity Ground Channel.\n");
        text.append("link: https://t.me/InfinityGround_AI");
        text.append("\n");
        text.append("\n");

        /*text.append("\uD83D\uDCEC <b>Report</b>\n");
        text.append("If you encounter any bugs or issues you can't handle, please submit them through the report link.\n");
        text.append("link: <a href='https://forms.gle/14rYrnTDt8dAXmvc7'>Cat Academia Bug Problem Report</a>");*/

        SendMessage message = new SendMessage();
        message.setText(text.toString());
        message.setParseMode("HTML");
        message.disableWebPagePreview();
        message.setChatId(chatId);
        try {
            absSender.executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //follow us 按钮
    private void sendFollowMessage(long chatId, String photoUrl, AbsSender absSender) {
        StringBuilder text = new StringBuilder();
        //text.append("\uD83D\uDC4F Join TG：https://t.me/InfinityG_Official\n\n");
        text.append("\uD83C\uDF81 Follow us and get more fun!\n");
        text.append("\n");
        text.append("\n");

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text.toString());
        sendPhotoRequest.setParseMode("HTML");

        List<InlineKeyboardButton> tgInline = new ArrayList<>();
        InlineKeyboardButton tgButton = new InlineKeyboardButton();
        tgButton.setText("\uD83D\uDC49 Join TG");
        tgButton.setUrl("https://t.me/CatAcademia_Announcement");
        tgInline.add(tgButton);

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
        backInlineKeyboardButtonList.add(tgInline);
        backInlineKeyboardButtonList.add(rowInline);
        backInlineKeyboardButtonList.add(rowInline2);
        backInlineKeyboardButtonList.add(rowInline3);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);

        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //invite 按钮
    private void sendInviteMessage(long chatId, String photoUrl, AbsSender absSender) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83C\uDF81<b>Submit feedback to earn more points and unlock amazing rewards!</b>\n");
        text.append("https://forms.gle/BtV2K6CAVhkFfaDe7");
        text.append("\n");
        text.append("\n");

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text.toString());
        sendPhotoRequest.setParseMode("HTML");

        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(long chatId, String text, AbsSender absSender) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendGame(long chatId, AbsSender absSender) {
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
            absSender.execute(sendGame);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void answerCallbackQuery(String callbackQueryId, String url, AbsSender absSender) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setUrl(url);

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //绑定钱包
    public void sendBindingMessage(long chatId, String photoUrl, AbsSender absSender) {
        String text = buildBindMsg();
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text);
        sendPhotoRequest.setParseMode("HTML");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDC49 Bind Wallet");
        button.setUrl(getBindWalletUrl(chatId));
        rowInline.add(button);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        KeyboardButton playBtn = playGameKeyboardBtn(PLAY_BTN_TXT, getPlayBtnUrl(chatId));
        KeyboardButton helpBtn = new KeyboardButton(HELP_BTN_TXT);
        KeyboardButton followBtn = new KeyboardButton(FOLLOW_US_BTN_TXT);
        //KeyboardButton inviteBtn = new KeyboardButton(INVITE_BTN_TXT);
        KeyboardButton bindWalletBtn = new KeyboardButton(BIND_WALLET_BTN_TXT);

        row1.add(playBtn);
        row1.add(bindWalletBtn);
        //row2.add(inviteBtn);
        row2.add(followBtn);
        row2.add(helpBtn);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.disableNotification();
        message.setText("Please bind your wallet!\uD83C\uDF81");
        message.setReplyMarkup(keyboardMarkup);
        try {
            absSender.executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //绑定钱包
    public void sendBindingBtnMessage(long chatId, String photoUrl, AbsSender absSender) {
        String text = buildBindMsg();
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text);
        sendPhotoRequest.setParseMode("HTML");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDC49 Bind Wallet");
        button.setUrl(getBindWalletUrl(chatId));
        rowInline.add(button);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);

        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String buildBindMsg() {
        StringBuilder text = new StringBuilder();
        text.append("Tip: After redirecting, click \"Wallet Login\" to connect your wallet.\uD83C\uDF81\n");
        text.append("We support 4 wallet logins: <b>OKX</b>, <b>Trust</b>, <b>Solflare</b>, <b>Phantom</b>.\n");
        text.append("\n");
        text.append("\n");
        return text.toString();
    }

    public String bindWalletProcess(String tid, AbsSender absSender) {
        return "";
    }

    public String getBindWalletUrl(Long chatId) {
        BotUser botUser = userService.getBotUser(chatId);
        String url = catBotConfig.getTgUrl() + "bind/%s";
        String path = String.format(url, botUser.encryptUserId());
        log.debug("getBindGamePathUrl  userId={},path={}", chatId, path);
        return path;
    }
}
