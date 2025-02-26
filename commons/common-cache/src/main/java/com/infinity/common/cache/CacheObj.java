package com.infinity.common.cache;

import com.infinity.common.consts.CachePrefixConsts;

public abstract class CacheObj {
    public abstract String getKeyName();

    public static String makeKeyName(String keyName) {
        return CachePrefixConsts.CACHE_PROFIX_OBJECT + keyName;
    }

    /**
     * map存储一组数据 map删除其中某个 map修改其中的一组
     *
     */
}
