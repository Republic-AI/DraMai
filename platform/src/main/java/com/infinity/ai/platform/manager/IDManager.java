package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.application.DBservice;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.utils.Snowflake;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.network.RequestIDManager;
import org.redisson.api.RedissonClient;

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
        //todo 机器ID 读取配置
        snow = new Snowflake(DBservice.machineId);
    }

    /**
     * 产生下一个ID
     *
     * @return ID
     */
    public long nextId() {
        return snow.nextId();
    }

    public long getId() {
        return RequestIDManager.getInstance().RequestID(false);
    }

    public long getId(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    public long getActionId() {
        return getId(RedisKeyEnum.ACTION_ID.getKey());
    }
}
