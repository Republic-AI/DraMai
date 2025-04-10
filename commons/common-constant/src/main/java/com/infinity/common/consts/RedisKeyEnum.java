package com.infinity.common.consts;

import org.redisson.api.RBucket;
import org.redisson.api.RSortedSet;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;


public enum RedisKeyEnum {

    CHAT("chat:list:%s", RBucket.class, new StringCodec()),
    MACHINE_ID("id:machine", null, null),
    SIGN_KEY("sign:%s", RBucket.class, new StringCodec()),
    ACTION_ID("id:action", null, null),
    CHAT_ID("id:chat", null, null),
    LIVE_ROOM("npc:live:room:%s", RSortedSet.class, new LongCodec()),
    LIVE_RANK("npc:live:rank:%s", RSortedSet.class, new LongCodec()),
    NICK_NAME_ID("id:nickname", null, null)
    ;

    private final String key;
    public final Class<?> redissonType;
    public final Codec codec;


    RedisKeyEnum(String key, Class<?> redissonType, Codec codec) {
        this.key = key;
        this.redissonType = redissonType;
        this.codec = codec;
    }

    public String getKey(Object... args) {
        return (args == null || args.length == 0) ? this.key : String.format(this.key, args);
    }
}
