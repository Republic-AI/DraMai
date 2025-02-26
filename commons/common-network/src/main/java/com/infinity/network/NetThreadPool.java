package com.infinity.network;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NetThreadPool implements ThreadFactory {
    private final ThreadGroup group_;
    private final AtomicInteger thread_number_ = new AtomicInteger(1);
    private final String name_prefix_;

    NetThreadPool(String id) {
        SecurityManager s = System.getSecurityManager();
        group_ = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        name_prefix_ = id + "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread runner = new Thread(group_, r,
                name_prefix_ + thread_number_.getAndIncrement(), 0);
        if (runner.isDaemon())
            runner.setDaemon(false);
        if (runner.getPriority() != Thread.NORM_PRIORITY)
            runner.setPriority(Thread.NORM_PRIORITY);

        return runner;
    }
}