package com.infinity.db.cache;

import com.infinity.db.util.FixedPriorityQueue;
import com.infinity.db.util.Pair;
import com.infinity.db.util.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CacheChecker extends Thread {
    public static final int interval = 60 * 1000;
    private long lastUp = System.currentTimeMillis();
    private volatile boolean running;
    private CacheStrategy strategy;

    public CacheChecker(CacheStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public synchronized void start() {
        if (!this.running) {
            this.running = true;
            super.start();
        }
        log.info("{} start", this.getName());
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                long now = System.currentTimeMillis();
                long delta = now - lastUp;
                if (delta < interval) {
                    Thread.sleep(interval - delta);
                    continue;
                }

                boolean stats = CacheMonitor.monitoring;
                int nowMin = (int) (System.currentTimeMillis() / 60000);

                Map<Class<?>, Pair<Long, Integer>> tszs = new HashMap<>();
                Map<Class<?>, Pair<Long, Integer>> vszs = new HashMap<>();
                Map<Class<?>, FixedPriorityQueue<Pair<Long, Integer>>> vszs_sz_top10 = new HashMap<>();
                Map<Class<?>, FixedPriorityQueue<Pair<Long, Integer>>> vszs_live_top10 = new HashMap<>();

                Pair<Long, Integer> a_tsz = new Pair<>(0L, 0);
                Pair<Long, Integer> a_vsz = new Pair<>(0L, 0);
                List<CacheObject> invalids = new ArrayList<>();
                Map<Class<?>, Map<Long, CacheObject>> tbcaches = CacheManager.get().getAll();
                for (Map.Entry<Class<?>, Map<Long, CacheObject>> entry : tbcaches.entrySet()) {
                    if (stats) {
                        tszs.put(entry.getKey(), new Pair<>(0L, 0));
                        vszs.put(entry.getKey(), new Pair<>(0L, 0));
                        vszs_sz_top10.put(entry.getKey(),
                                new FixedPriorityQueue<>(new Comparator<Pair<Long, Integer>>() {
                                    @Override
                                    public int compare(Pair<Long, Integer> o1, Pair<Long, Integer> o2) {
                                        return o2.second - o1.second;
                                    }
                                }, 10));
                        vszs_live_top10.put(entry.getKey(),
                                new FixedPriorityQueue<>(new Comparator<Pair<Long, Integer>>() {
                                    @Override
                                    public int compare(Pair<Long, Integer> o1, Pair<Long, Integer> o2) {
                                        return o2.second - o1.second;
                                    }
                                }, 10));
                        if (!CacheMonitor.inst.sz_max_history.containsKey(entry.getKey())) {
                            CacheMonitor.inst.sz_max_history.put(entry.getKey(), new Pair<>(0L, 0L));
                            CacheMonitor.inst.invalid_max_history.put(entry.getKey(), new Pair<>(0L, 0L));
                            CacheMonitor.inst.sz_max_history_1.put(entry.getKey(), new Pair<>(0L, 0L));
                        }
                        if (!CacheMonitor.inst.orphan_history.containsKey(entry.getKey())) {
                            CacheMonitor.inst.orphan_history.put(entry.getKey(), new Tuple<>(0L, 0L, 0L));
                        }
                    }

                    for (Map.Entry<Long, CacheObject> entry1 : entry.getValue().entrySet()) {
                        if (stats) {
                            tszs.get(entry.getKey()).first += entry1.getValue().getSz().obj;
                            tszs.get(entry.getKey()).second += 1;
                            a_tsz.second += 1;
                            if (entry1.getValue().getSz().obj > CacheMonitor.inst.sz_max_history_1.get(entry.getKey()).first) {
                                CacheMonitor.inst.sz_max_history_1.get(entry.getKey()).first = entry1.getValue().getSz().obj.longValue();
                                CacheMonitor.inst.sz_max_history_1.get(entry.getKey()).second = entry1.getKey();
                            }
                        }

                        if (!strategy.valid(entry1.getValue())) {
                            invalids.add(entry1.getValue());
                            continue;
                        }

                        if (stats) {
                            vszs.get(entry.getKey()).first += entry1.getValue().getSz().obj;
                            vszs.get(entry.getKey()).second += 1;
                            a_vsz.second += 1;
                            vszs_sz_top10.get(entry.getKey())
                                    .add(new Pair<>(entry1.getKey(), entry1.getValue().getSz().obj));
                            vszs_live_top10.get(entry.getKey())
                                    .add(new Pair<>(entry1.getKey(), nowMin - entry1.getValue().getCreateMin()));
                        }
                    }
                    if (stats) {
                        a_vsz.first += vszs.get(entry.getKey()).first;
                        a_tsz.first += tszs.get(entry.getKey()).first;
                        if (tszs.get(entry.getKey()).first > CacheMonitor.inst.sz_max_history
                                .get(entry.getKey()).first) {
                            CacheMonitor.inst.sz_max_history.get(entry.getKey()).first = tszs.get(entry.getKey()).first;
                            CacheMonitor.inst.sz_max_history.get(entry.getKey()).second = now;
                        }
                        if (tszs.get(entry.getKey()).first
                                - vszs.get(entry.getKey()).first > CacheMonitor.inst.invalid_max_history
                                .get(entry.getKey()).first) {
                            CacheMonitor.inst.invalid_max_history
                                    .get(entry.getKey()).first = tszs.get(entry.getKey()).first
                                    - vszs.get(entry.getKey()).first;
                            CacheMonitor.inst.invalid_max_history.get(entry.getKey()).second = now;
                        }
                    }
                }
                for (CacheObject co : invalids) {
                    CacheObject tmp = CacheManager.get().invalid(co.getData().getClass(), co.getData().getId());
                    if (stats) {
                        if (tmp != null && !tmp.isGrabExpire()) {
                            CacheMonitor.inst.orphan_history.get(tmp.getData().getClass()).first += 1;
                            CacheMonitor.inst.orphan_history.get(tmp.getData().getClass()).second = tmp.getData()
                                    .getId();
                            CacheMonitor.inst.orphan_history.get(tmp.getData().getClass()).third = now;
                        }
                    }
                }

                invalids.clear();
                lastUp = now;

                if (stats) {
                    if (a_tsz.first > CacheMonitor.inst.total_sz_max_history.first) {
                        CacheMonitor.inst.total_sz_max_history.first = a_tsz.first;
                        CacheMonitor.inst.total_sz_max_history.second = now;
                    }
                    if (a_tsz.first - a_vsz.first > CacheMonitor.inst.total_invalid_max_history.first) {
                        CacheMonitor.inst.total_invalid_max_history.first = a_tsz.first - a_vsz.first;
                        CacheMonitor.inst.total_invalid_max_history.second = now;
                    }
                    CacheMonitor.printStats(tszs, vszs, vszs_sz_top10, vszs_live_top10, a_tsz, a_vsz);
                }
            } catch (InterruptedException e0) {
                // ignore
            } catch (Exception e) {
                log.error("CacheChecker fail", e);
            }
        }
        log.info("{} closed", this.getName());
    }

    public void close() {
        this.running = false;
        log.info("{} closing", this.getName());
        this.interrupt();
    }

}
