package com.infinity.network;

import java.util.concurrent.atomic.AtomicInteger;

public class ChannelIDManager {
    private final AtomicInteger id_ = new AtomicInteger(1);

    ChannelIDManager() {
    }

    public int newID() {
        return id_.getAndIncrement() % Integer.MAX_VALUE;
    }
}