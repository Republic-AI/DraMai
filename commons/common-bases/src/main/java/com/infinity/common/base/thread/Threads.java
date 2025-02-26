package com.infinity.common.base.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Threads {
    private static Map<String, QueueThread[]> queues = new HashMap<>();
    private static Map<String, TimerThread[]> timers = new HashMap<>();
    static final String CHECK_NameThread_DEADLOCK = "CHECK_NameThread_DEADLOCK";

    static {
        regist(new NameParam(CHECK_NameThread_DEADLOCK, 1, 60 * 1000, false));
    }

    public synchronized static void regist(MultiParam<NameParam> ths) {
        for (var th : ths.params()) {
            regist(th);
        }
    }

    public synchronized static void regist(NameParam param) {
        if (timers.containsKey(param.getName())) {
            throw new IllegalStateException(String.format("group name[%s] exist", param.getName()));
        }
        if (queues.containsKey(param.getName())) {
            throw new IllegalStateException(String.format("group name[%s] exist", param.getName()));
        }
        if (param.getInterval() > 0) {
            TimerThread[] threads = new TimerThread[param.getNum()];
            for (int i = 0; i < param.getNum(); i++) {
                TimerThread t = new TimerThread(param.getName() + "_" + i, param.getInterval(), param.isCheckdead());
                threads[i] = t;
                t.start();
            }
            timers.put(param.getName(), threads);
        } else {
            QueueThread[] threads = new QueueThread[param.getNum()];
            for (int i = 0; i < param.getNum(); i++) {
                QueueThread t = new QueueThread(param.getName() + "_" + i, param.isCheckdead());
                threads[i] = t;
                t.start();
            }
            queues.put(param.getName(), threads);
        }
    }

    public synchronized static void dispose() {
        for (var e0 : queues.entrySet()) {
            for (var t : e0.getValue()) {
                t.close();
            }
        }
        for (var e0 : timers.entrySet()) {
            for (var t : e0.getValue()) {
                t.close();
            }
        }
    }

    public synchronized static void dispose(String groupName) {
        NameThread[] ths = null;
        if (queues.containsKey(groupName)) {
            ths = queues.remove(groupName);
        } else {
            ths = timers.remove(groupName);
        }
        if (ths != null) {
            for (var th : ths) {
                th.close();
            }
        }
    }

    /**
     * run use random thread
     */
    public static int addListener(String groupName, String listenerName, TimerListener listener) {
        var ths = timers.get(groupName);
        return ths[ths.length == 1 ? 0 : ThreadLocalRandom.current().nextInt(ths.length)]
                .add(new NameTask<>(listenerName, listener));
    }

    public static void removeListener(String groupName, long id, int lid) {
        var ths = timers.get(groupName);
        ths[ths.length == 1 ? 0 : (int) Math.abs(id % ths.length)].remove(lid);
    }

    public static int addListener(String groupName, long id, String listenerName, TimerListener listener) {
        var ths = timers.get(groupName);
        return ths[ths.length == 1 ? 0 : (int) Math.abs(id % ths.length)].add(new NameTask<>(listenerName, listener));
    }

    /**
     * run use random thread
     */
    public static void runAsync(String groupName, String taskName, QueueTask task) {
        var ths = queues.get(groupName);
        ths[ths.length == 1 ? 0 : ThreadLocalRandom.current().nextInt(ths.length)].add(new NameTask<>(taskName, task));
    }

    public static void runAsync(String groupName, long id, String taskName, QueueTask task) {
        var ths = queues.get(groupName);
        ths[ths.length == 1 ? 0 : (int) Math.abs(id % ths.length)].add(new NameTask<>(taskName, task));
    }
}
