package com.infinity.db.cache;

public final class TtlStrategy extends CacheStrategy {

    @Override
    void update(CacheObject co) {
        if (co.getData().getCacheExpire() < 0)
            return;

        if (co.getExpire() == -1) {
            int now = (int) (System.currentTimeMillis() / 1000);
            co.setExpire(now + co.getData().getCacheExpire() * 60);
        }
    }

    @Override
    boolean valid(CacheObject co) {
        if (co.getExpire() < 0)
            return true;

        return System.currentTimeMillis() / 1000 < co.getExpire() || !co.isGrabExpire();
    }
}