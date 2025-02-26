package com.infinity.db.cache;

public abstract class CacheStrategy {
    abstract void update(CacheObject co);

    abstract boolean valid(CacheObject co);
}
