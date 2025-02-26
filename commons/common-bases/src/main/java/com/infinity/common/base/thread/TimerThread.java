package com.infinity.common.base.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class TimerThread extends NameThread {
    private int interval;
    private long lastUp = System.currentTimeMillis();
    private Map<Integer, NameTask<TimerListener>> listeners = new ConcurrentHashMap<>();
    private AtomicInteger id = new AtomicInteger(0);

    public TimerThread(String name, int interval, boolean checkdead) {
        super(name, checkdead);
        this.interval = interval;
    }

    public int add(NameTask<TimerListener> listener) {
        int lid = id.addAndGet(1);
        if (this.listeners.putIfAbsent(lid, listener) != null) {
            log.error("timerthread{}: add listener{} fail, why exist so many listeners?", this.getName(), listener.name);
            lid = -1;
        }
        return lid;
    }

    public void remove(int lid) {
        this.listeners.remove(lid);
    }

    @Override
    protected void run0() throws Exception {
        long now = System.currentTimeMillis();
        long delta = now - lastUp;
        if (delta < interval) {
            Thread.sleep(interval - delta);
            return;
        }
        dispatch();

        /*long cost = System.currentTimeMillis() - now;
        if (cost <= interval) {
            lastUp = now;
        } else {
            lastUp = now + (this.interval - cost);
        }*/

        this.lastUp = System.currentTimeMillis();
    }

    private void dispatch() {
        try {
            Set<Integer> rms = null;
            for (var e0 : this.listeners.entrySet()) {
                long now = mark(e0.getValue().name);
                try {
                    if (e0.getValue().get().exec(interval)) {
                        if (rms == null)
                            rms = new HashSet<>();
                        rms.add(e0.getKey());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error("timerlistener[{}], err2: {}", e0.getValue().name, ex.getMessage());
                }

                long tm = System.currentTimeMillis() - now;
                if (tm > 30) {
                    log.warn("timerlistener[{}] exec cost %d ms beyond 30 ms", e0.getValue().name, tm);
                    ThreadMonitor.report(false, this.getName(), e0.getValue().name, 1, tm, -1, -1);
                }

                ThreadMonitor.report(true, this.getName(), e0.getValue().name, 1, tm, -1, -1);
                mark(null);
            }

            if (rms != null) {
                for (Integer k : rms) {
                    this.listeners.remove(k);
                }
            }
        } catch (Exception ex) {
            log.error("timer[{}], err1: {}", this.getName(), ex.getMessage());
        }

    }

}
