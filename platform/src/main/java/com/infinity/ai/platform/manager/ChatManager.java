package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.entity.NpcChatData;
import com.infinity.ai.platform.repository.NpcChatDataRepository;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.msg.platform.npc.ChatDataVo;
import com.infinity.common.utils.spring.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;

public class ChatManager {

    private Map<String, NpcChatData> chatDataMap = new HashMap<>();

    private static ChatManager instance;

    private ChatManager() {
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void talk(long sender, long receiver, String content) {
        String key = getKey(sender, receiver);
        NpcChatData npcChatData = chatDataMap.get(key);
        if (npcChatData == null) {
            npcChatData = new NpcChatData();
            npcChatData.setGameTime(MapDataManager.getInstance().getGameTime());
            npcChatData.setCreatedAt(System.currentTimeMillis());
            chatDataMap.put(key, npcChatData);
        }
        npcChatData.getSpeakDataList().add(new ChatDataVo(sender, receiver, content));
    }

    public void endTalk(long sender, long receiver) {
        String key = getKey(sender, receiver);
        NpcChatData npcChatData = chatDataMap.get(key);
        if (npcChatData != null) {
            Threads.runAsync(ThreadConst.QUEUE_LOGIC, "Async#saveChat", () -> {
                if (npcChatData.getSpeakDataList().size() > 1) {
                    SpringContextHolder.getBean(NpcChatDataRepository.class).save(npcChatData);
                }
            });
            chatDataMap.remove(key);
        }
    }


    public String getKey(long sender, long receiver) {
        long first = Math.min(sender, receiver);  // 确保较小的值在前
        long second = Math.max(sender, receiver); // 确保较大的值在后
        return first + "_" + second;  // 生成 key
    }
}
