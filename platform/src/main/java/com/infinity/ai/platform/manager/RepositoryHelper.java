package com.infinity.ai.platform.manager;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.ActionLog;
import com.infinity.ai.domain.model.Chat;
import com.infinity.ai.service.IActionRepository;
import com.infinity.ai.service.IChatRepository;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryHelper {
    public static final String chatType = "m";

    public static void addChat(Long sender, Long targetId, String content, int type) {
        if (StringUtils.isEmpty(content)) {
            log.debug("save chat record fail, content is null, sender={},targetId={}", sender, targetId);
            return;
        }

        Chat chat = Chat.builder()
                .content(content)
                .senderId(sender)
                .targetId(Long.valueOf(targetId))
                .msgType(type)
                .createTime(System.currentTimeMillis())
                .build();
        chat.generateConversationId(chatType);

        IChatRepository chatRepository = SpringContextHolder.getBean(IChatRepository.class);
        chatRepository.add(chat);
    }

    public static void addActionLog(ActionData actionData) {
        if (actionData ==null) {
            return;
        }

        ActionLog actionLog = ActionLog.builder()
                .id(actionData.getId())
                .npcId(actionData.getNpcId())
                .aid(actionData.getAid())
                .paid(actionData.getPaid())
                .startTime(actionData.getStartTime())
                .endTime(actionData.getEndTime())
                .content(actionData.getContent())
                .createTime(System.currentTimeMillis())
                .build();

        IActionRepository repository = SpringContextHolder.getBean(IActionRepository.class);
        repository.add(actionLog);
    }

    public static void addActionLog(Long npcId, int aid, Long paid, Long sTime, Long eTime, String content) {
        if (npcId <= 0 || aid <= 0) {
            log.debug("save chat record fail, npcId or aid formate error, npcId={},aid={}", npcId, aid);
            return;
        }

        ActionLog actionLog = ActionLog.builder()
                .id(IDManager.getInstance().nextId())
                .npcId(npcId)
                .aid(aid)
                .paid(paid)
                .startTime(sTime)
                .endTime(eTime)
                .content(content)
                .createTime(System.currentTimeMillis())
                .build();

        IActionRepository repository = SpringContextHolder.getBean(IActionRepository.class);
        repository.add(actionLog);
    }
}
