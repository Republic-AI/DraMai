package com.infinity.ai.chat.manager;

import com.infinity.ai.chat.common.FixedWindowRateLimiter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChatManager {

    private static class Holder {
        private static final ChatManager kInstance = new ChatManager();
    }

    public static ChatManager getInstance() {
        return Holder.kInstance;
    }

    private Map<Long, FixedWindowRateLimiter> limiterMap = new ConcurrentHashMap<>();


    public void init() {

    }

    public void reflushChatGPT() {

    }

    public void dispose() {

    }

    public boolean canChat(long playerId) {
        FixedWindowRateLimiter fixedWindowRateLimiter = limiterMap.get(playerId);
        if (fixedWindowRateLimiter == null) {
            fixedWindowRateLimiter = new FixedWindowRateLimiter(10, 5000);
            limiterMap.put(playerId, fixedWindowRateLimiter);
        }
        return fixedWindowRateLimiter.tryAcquire();
    }

}
