package com.infinity.network;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WriteCache {
    private final Lock cache_lock_;
    private final LinkedList<ByteBuffer> cache_;

    public WriteCache() {
        cache_ = new LinkedList<ByteBuffer>();
        cache_lock_ = new ReentrantLock();
    }

    public boolean isEmpty() {
        cache_lock_.lock();
        boolean bEmpty = cache_.isEmpty();
        cache_lock_.unlock();
        return bEmpty;
    }

    public void push(ByteBuffer newData) {
        cache_lock_.lock();
        cache_.addLast(newData);
        cache_lock_.unlock();
    }

    public ByteBuffer peek() {
        cache_lock_.lock();
        ByteBuffer result = cache_.getFirst();
        cache_lock_.unlock();
        return result;
    }

    public ByteBuffer pop() {
        cache_lock_.lock();
        ByteBuffer sendData = cache_.getFirst();
        cache_.removeFirst();
        cache_lock_.unlock();
        return sendData;
    }

    public int size() {
        cache_lock_.lock();
        int size = cache_.size();
        cache_lock_.unlock();
        return size;
    }
}