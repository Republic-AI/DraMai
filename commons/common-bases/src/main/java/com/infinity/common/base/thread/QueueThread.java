package com.infinity.common.base.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class QueueThread extends NameThread {
    private final BlockingQueue<NameTask<QueueTask>> tasks = new LinkedBlockingQueue<>();

    public QueueThread(String name, boolean checkdead) {
        super(name, checkdead);
    }

    public void add(NameTask<QueueTask> task) {
        this.tasks.add(task);
    }

    @Override
    public void run0() throws Exception {
        var task = this.tasks.take();
        long now = mark(task.name);
        task.get().exec();
        long tm = System.currentTimeMillis() - now;
        if (tm > 50) {
            log.warn("queuetask[{}] exec cost {} ms beyond 50 ms", task.name, tm);
            ThreadMonitor.report(false, this.getName(), task.name, 1, tm, -1, -1);
        }

        ThreadMonitor.report(true, this.getName(), task.name, 1, tm, this.tasks.size(), now - task.createTm);
        mark(null);
    }

    public void close() {
        while (!this.tasks.isEmpty())
            ;
        super.close();
    }
}
