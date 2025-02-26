package com.infinity.ai.chat.manager;

import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.utils.Snowflake;
import com.infinity.common.utils.spring.SpringContextHolder;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ThreadLocalRandom;

public class IDManager {
    private Snowflake snow;
    private RedissonClient redissonClient;

    private static final class IDManagerHolder {
        private static final IDManager kInstance = new IDManager();
    }

    public static IDManager getInstance() {
        return IDManagerHolder.kInstance;
    }

    private IDManager() {
        init();
        redissonClient = SpringContextHolder.getBean(RedissonClient.class);
    }

    public void init() {
        snow = new Snowflake(ThreadLocalRandom.current().nextInt(20,40));
    }

    /**
     * 产生下一个ID
     *
     * @return ID
     */
    public long nextId() {
        return snow.nextId();
    }

    public long getId(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    public long getChatId() {
        return getId(RedisKeyEnum.CHAT_ID.getKey());
    }
}
