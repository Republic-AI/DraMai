package com.infinity.ai.login.common;

public class FixedWindowRateLimiter {
    private final int limit;
    private final long windowSize;
    private long windowStart;
    private int count;

    public FixedWindowRateLimiter(int limit, long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
        this.windowStart = System.currentTimeMillis();
        this.count = 0;
    }

    public boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - windowStart > windowSize) {
            windowStart = now;
            count = 0;
        }
        if (count < limit) {
            count++;
            return true;
        }
        return false;
    }
}