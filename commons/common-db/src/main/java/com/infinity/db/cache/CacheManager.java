package com.infinity.db.cache;

public class CacheManager {
    private static JvmCache jc;
    private static CacheWorker[] caWorkers;
    private static CacheChecker caCheck;

    public static void init(int maxn, boolean async, CacheStrategy strategy) {
        jc = new JvmCache(async, strategy);
        caCheck = new CacheChecker(strategy);
        caCheck.setName("CacheChecker");
        caCheck.start();
        caWorkers = new CacheWorker[maxn];
        for (int i = 0; i < maxn; i++) {
            caWorkers[i] = new CacheWorker();
            caWorkers[i].setName("CacheWorker" + i);
            caWorkers[i].start();
        }
    }

    public static CacheWorker getWorker(long id) {
        return caWorkers[(int) (id % caWorkers.length)];
    }

    public static void close() {
        for (CacheWorker cw : caWorkers) {
            cw.close();
        }
        caCheck.close();
    }

    public static JvmCache get() {
        return jc;
    }
}
