package com.infinity.db.cache;

import com.infinity.db.db.DBManager;
import com.infinity.db.db.DBSqlType;
import com.infinity.db.except.DBDirtyException;
import com.infinity.db.util.ObjUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CacheWorker extends Thread {
    private int interval = 30 * 1000;
    private volatile boolean running;
    private Map<CacheObject, Object> watchs = new ConcurrentHashMap<>();
    private volatile boolean wheel;
    private final Object lock = new Object();
    private Map<CacheObject, Object> immes = new ConcurrentHashMap<>();
    private long immeing;
    private long nextTm;
    private long fails;
    private long dirts;

    public void add(CacheObject co) {
        watchs.put(co, lock);
    }

    @Override
    public synchronized void start() {
        if (!this.running) {
            this.running = true;
            this.wheel = true;
            this.nextTm = System.currentTimeMillis() + interval;
            super.start();
        }
        log.info("{} start", this.getName());
    }

    public void immediate(CacheObject co) {
        if (co.isDirty())
            return;

        if (immes.putIfAbsent(co, this.lock) != null)
            return;

        synchronized (this.lock) {
            immeing += 1;
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                dosomthing();
                if (!this.running)
                    break;

                synchronized (this.lock) {
                    if (immeing > 0) {
                        lock.wait(10);
                    } else {
                        lock.wait(Math.max(0, this.nextTm - System.currentTimeMillis()));
                    }
                }
            } catch (InterruptedException e0) {
                // ignore
            } catch (Exception e) {
                log.error("CacheWorker fail", e);
                wheel = true;
            }
        }
        log.info("{} closed, fails[{}], dirts[{}], immeing[{}], watchs[{}]", this.getName(), fails,
                dirts, immeing,
                watchs.size());
    }

    private long persist(CacheObject co, long n) {
        if (co.isDirty() || co.isReadonly())
            return n;

        try {
            switch (co.getOpt()) {
                case INSERT:
                    co.setLastMd5(ObjUtils.objectMd5(co.getData(), co.getSz()));
                    DBManager.get().save(co.getData(), false);
                    co.setOpt(DBSqlType.UPDATE, true);
                    n = (((n >> 42) + 1) << 42) | (n & ~(-1L << 42));
                    break;
                case UPDATE:
                    String md5 = ObjUtils.objectMd5(co.getData(), co.getSz());
                    if (!md5.equals(co.getLastMd5())) {
                        co.setLastMd5(md5);
                        DBManager.get().save(co.getData(), true);
                        n = (((n >> 21) + 1) << 21) | (n & ~(-1L << 21));
                    }
                    break;
                case DELETE:
                    DBManager.get().delete(co.getData().getClass(), co.getData().getId());
                    co.setDirty();
                    n += 1;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            if (e instanceof DBDirtyException) {
                co.setDirty();
                CacheManager.get().expire(co.getData().getClass(), co.getData().getId());
                dirts += 1;
            }

            fails += 1;
            log.error("persist: fails[{}], dirts[{}], curopt[{}]: '{}'", fails, dirts,
                    co.getOpt().name(), e.getMessage(), e.getCause());
        }
        return n;
    }

    private void dosomthing() throws Exception {
        if (!wheel)
            return;

        wheel = false;
        long n = 0;
        boolean whole = true;
        do {
            var it0 = this.immes.entrySet().iterator();
            long tmpImmeing = 0;
            while (it0.hasNext()) {
                var co = it0.next().getKey();
                n = persist(co, n);
                it0.remove();
                tmpImmeing += 1;
            }

            synchronized (this.lock) {
                this.immeing -= tmpImmeing;
            }

            if (this.nextTm - System.currentTimeMillis() > 0) {
                whole = false;
                break;
            }

            var it = this.watchs.entrySet().iterator();
            while (it.hasNext()) {
                var co = it.next().getKey();
                n = persist(co, n);
                if (co.isInvalid()) {
                    it.remove();
                }
            }

            this.nextTm = System.currentTimeMillis() + interval;
        } while (false);
        wheel = true;
        log.debug("CacheWorker whole[{}], inserts: {}, updates: {}, deletes: {}", whole, n >> 42,
                (n >> 21) & ~(-1L << 21),
                n & ~(-1L << 21));
    }

    public void close() {
        this.running = false;
        try {
            dosomthing();
        } catch (Exception e) {
            log.error("CacheWorker fail", e);
        }

        log.info("{} closing", this.getName());
        synchronized (this.lock) {
            this.lock.notify();
        }
    }
}
