package com.infinity.ai.telegram.service;

import com.infinity.ai.domain.model.TGUser;
import com.infinity.ai.service.IPlayerRepository;
import com.infinity.ai.telegram.bot.BotUser;
import com.infinity.ai.telegram.common.Constant;
import com.infinity.ai.telegram.config.CatBotConfig;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.consts.LoginType;
import com.infinity.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private IPlayerRepository repository;

    @Autowired
    private CatBotConfig config;

    public BotUser getBotUser(Update update) {
        User user = update.getMessage().getFrom();
        return BotUser.builder().id(user.getId())
                .userId("TG" + user.getId())
                .name(user.getFirstName() + user.getLastName())
                .inviteCode("b" + user.getId() + "c")
                .pwd("123")
                .build();
    }

    public BotUser getBotUser(Long chatId) {
        return BotUser.builder().id(chatId)
                .userId("TG" + chatId)
                .name("")
                .inviteCode("b" + chatId + "c")
                .pwd("123")
                .build();
    }

    //是否首次登录
    /*public boolean haveLogin(String tid) {
        Long playerId = repository.findIdByUionId(tid, LoginType.TG_BOT.name());
        return (playerId != null && playerId > 0);
    }

    //是否绑定钱包:true:已绑定，false:未绑定
    public boolean haveWallet(String tid) {
        int count = repository.tgUserHasBindWallet(tid);
        return count > 0;
    }*/

    public void handleStart(long chatId, String messageText, String name) {
        // 分离 /start 和 参数
        String[] commandParts = messageText.split(" ", 2);
        if (commandParts.length > 1) {
            //用户来源
            String source = commandParts[1];
            // 处理参数
            log.debug("chatId={},source={}", chatId, source);
            saveSource(chatId, source, name);
        } else {
            saveSource(chatId, config.getDefaultSrc(), name);
        }
    }

    private void saveSource(long chatId, String source, String name) {
        if (chatId <= 0 || StringUtils.isEmpty(source)) {
            return;
        }

        Threads.runAsync(ThreadConst.QUEUE_LOGIC, chatId, "save#source", () -> {
            Integer count = repository.countSource(chatId);
            if (count <= 0) {
                long now = System.currentTimeMillis();
                TGUser user = TGUser.builder().chatId(chatId)
                        .name(name)
                        .source(source)
                        .state(0)
                        .createdate(now)
                        .updateTime(now)
                        .deleted(0).build();
                repository.addPlayerSource(user);
            }
        });
    }

    public void push(AbsSender absSender) {
        Integer total = repository.countUnSendTGUser();
        if (total > 0) {
            int pageSize = 100;
            int totalPage = (total % pageSize == 0) ? total / pageSize : (total / pageSize + 1);

            for (int i = 1; i <= totalPage; i++) {
                int pageIndex = (i - 1) * pageSize;
                List<TGUser> userList = repository.queryTGUserList(pageIndex, pageSize);
                if (userList == null || userList.size() == 0) {
                    break;
                }

                sendMessage(userList, absSender);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(List<TGUser> userList, AbsSender absSender) {
        for (TGUser user : userList) {
            if (user == null || user.getState() == 1) {
                continue;
            }
            SendPhoto sendPhoto = buildSendPhoto(user.getChatId(), Constant.PUSH_IMG);
            sendStartMessage(sendPhoto, absSender);
        }

        //更新状态
        List<Long> ids = userList.stream().map(TGUser::getId).collect(Collectors.toList());
        repository.updateTGUserState(ids);
    }

    //start 按钮
    private void sendStartMessage(SendPhoto sendPhotoRequest, AbsSender absSender) {
        try {
            absSender.executeAsync(sendPhotoRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SendPhoto buildSendPhoto(long chatId, String photoUrl) {
        StringBuilder text = new StringBuilder();
        text.append("\uD83D\uDD08 Dear Purrfessors: \n");
        text.append(" The Cat Academia Bot is now upgraded! \n");
        text.append(" Click the button below to update your game.\uD83D\uDC47\n");
        text.append("\n");
        text.append("\n");

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(photoUrl));
        sendPhotoRequest.setCaption(text.toString());
        sendPhotoRequest.setParseMode("HTML");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton refreshBtn = new InlineKeyboardButton();
        refreshBtn.setText("Update Bot");
        refreshBtn.setCallbackData("/start");
        rowInline.add(refreshBtn);

        List<List<InlineKeyboardButton>> backInlineKeyboardButtonList = new ArrayList<List<InlineKeyboardButton>>();
        backInlineKeyboardButtonList.add(rowInline);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(backInlineKeyboardButtonList);
        sendPhotoRequest.setReplyMarkup(inlineKeyboardMarkup);
        return sendPhotoRequest;
    }
}
