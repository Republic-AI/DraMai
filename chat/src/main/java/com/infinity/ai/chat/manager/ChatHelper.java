package com.infinity.ai.chat.manager;

import com.infinity.ai.domain.model.Chat;
import com.infinity.ai.service.IChatRepository;
import com.infinity.common.msg.chat.ChatRequest;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatHelper {
    public static final String chatType = "1";

    public static void addChat(ChatRequest msg) {
        addChat(msg.getPlayerId(),
                Long.valueOf(msg.getData().getNpcId()),
                msg.getData().getContext(),
                1,
                msg.getData().getBarrage(),
                msg.getData().getSender());
    }

    public static void addChat(Long sender, Long targetId, String content, int type, int barrage, String sname) {
        if (StringUtils.isEmpty(content)) {
            log.debug("save chat record fail, content is null, sender={},targetId={}", sender, targetId);
            return;
        }

        Chat chat = Chat.builder()
                .content(content)
                .senderId(sender)
                .targetId(Long.valueOf(targetId))
                .msgType(type)
                .barrage(barrage)
                .sname(sname)
                .tname("")
                .createTime(System.currentTimeMillis())
                .build();
        chat.generateConversationId(chatType);

        IChatRepository chatRepository = SpringContextHolder.getBean(IChatRepository.class);
        chatRepository.add(chat);
    }
}
