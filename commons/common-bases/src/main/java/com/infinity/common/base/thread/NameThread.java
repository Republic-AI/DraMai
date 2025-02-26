package com.infinity.common.base.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class NameThread extends Thread {
    protected volatile boolean running;
    private long execTm = -1L;
    private String execName = null;


    public NameThread(String name, boolean checkdead) {
        this.setName(name);
        if (checkdead) {
            Threads.addListener(Threads.CHECK_NameThread_DEADLOCK, "check_deadlock", this::check_deadlock);
        }
    }

    @Override
    final public synchronized void start() {
        if (!this.running) {
            this.running = true;
            super.start();
        }
        log.info("{} start", this.getName());
    }

    public void close() {
        if (!this.running)
            return;
        this.running = false;
        log.info("{} closing", this.getName());
        this.interrupt();
    }

    @Override
    final public void run() {
        while (this.running) {
            try {
                this.run0();
            } catch (InterruptedException e) {
                e.printStackTrace();
                // ignore
            } catch (Exception e) {
                e.printStackTrace();
                log.error("{} err: {}", this.getName(), e.getMessage());
            }
        }
        log.info("{} closed", this.getName());
    }

    protected abstract void run0() throws Exception;

    private boolean check_deadlock(int interval) {
        if (!running)
            return true;
        long tmp = this.execTm;
        if (tmp > 0) {
            long tm = System.currentTimeMillis() - tmp;
            if (tm >= 60 * 1000) {
                log.error("{}[{}] may be deadlock, execing {} ms", this.getName(), this.execName, tm);
            }
        }
        ThreadMonitor.printStats();
        return false;
    }

    final protected long mark(String execName) {
        this.execTm = execName == null ? -1 : System.currentTimeMillis();
        this.execName = execName;
        return this.execTm;
    }
}
