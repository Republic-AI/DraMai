package com.infinity.common.base.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ThreadMonitor {
    long slow_times;
    long slow_tm;
    long slow_max;
    long accum_times;
    long accum_tm;
    long accum_wait;
    long wait_max;
    long wait_tm;
    long wait_tasks;
    long wait_times;

    private ThreadMonitor() {

    }

    private static Map<String, Map<String, ThreadMonitor>> costs = new HashMap<>();

    private static long printCostTm = -1;
    private static int PRINT_MIN = 1;
    private static long monitorTm = -1;
    private static boolean monitoring = false;

    public static void monitor(long now, int min, int printMin) {
        if (min == -1 || min > 0) {
            printMin = Math.max(1, printMin);
            monitorOn(now, min, printMin);
        } else {
            monitorOff(-1);
        }
    }

    private static void monitorOn(long now, int min, int printMin) {
        monitorTm = min == -1 ? -1 : now + min * 60 * 1000;
        PRINT_MIN = printMin;
        printCostTm = now + printMin * 60 * 1000;
        monitoring = true;
        log.info("thead monitor [on]");
    }

    private static boolean monitorOff(long now) {
        if (!monitoring)
            return true;
        if (now == -1 || (monitorTm != -1 && now >= monitorTm)) {
            monitoring = false;
            for (var e0 : costs.entrySet()) {
                e0.getValue().clear();
            }
            log.info("thead monitor [off]");
            return true;
        }
        return false;
    }

    private static boolean canPrint(long now) {
        if (monitorOff(now))
            return false;
        if (printCostTm > now) {
            return false;
        }
        printCostTm = now + PRINT_MIN * 60 * 1000;
        return true;
    }

    public static void report(boolean accum, String thd, String name, long times, long tm, long waitTasks,
            long waitTm) {
        if (!monitoring)
            return;
        report(accum, 1, thd, name, times, tm, waitTasks, waitTm,
                -1, -1, null);
    }

    private static void report(boolean accum, int maxed, String thd, String name,
            long times, long tm,
            long waitTasks, long waitTm,
            long waitTimes, long accumWait,
            Map<String, ThreadMonitor> cost) {
        if (cost == null) {
            cost = costs.get(thd);
            if (cost == null) {
                Map<String, ThreadMonitor> h = new ConcurrentHashMap<>();
                cost = costs.putIfAbsent(thd, h);
                if (cost == null)
                    cost = h;
            }
        }

        ThreadMonitor old = cost.get(name);
        if (old == null) {
            old = new ThreadMonitor();
            cost.put(name, old);
        }
        if (accum) {
            if (maxed > 0 && waitTm > old.wait_max) {
                old.wait_max = waitTm;
                old.wait_tasks = waitTasks;
            }
            if (maxed >= 0 && times > 0) {
                old.accum_times += times;
                old.accum_tm += tm;
                if (waitTm >= 0) {
                    old.accum_wait += (accumWait == -1 ? waitTm : accumWait);
                    if (waitTimes == -1) {
                        if (waitTm > 30) {
                            old.wait_times += 1;
                            old.wait_tm += waitTm;
                        }
                    } else {
                        old.wait_times += waitTimes;
                        old.wait_tm += waitTm;
                    }
                }
            }
        } else {
            if (maxed > 0 && tm > old.slow_max) {
                old.slow_max = tm;
            }
            if (maxed >= 0 && times > 0) {
                old.slow_times += times;
                old.slow_tm += tm;
            }
        }
    }

    static void printStats() {
        if (!canPrint(System.currentTimeMillis()))
            return;

        StringBuffer sb = new StringBuffer();
        sb.append("\n***************thread stat start**************").append("\n");
        Map<String, ThreadMonitor> allcost = new HashMap<>();
        for (var e0 : costs.entrySet()) {
            sb.append(dumpthd(e0.getKey(), e0.getValue(), allcost));
        }
        sb.append(dumpthd("ALL", allcost, null));
        sb.append("***************thread stat end**************");
        log.info(sb.toString());
    }

    private static StringBuffer dumpthd(String thd, Map<String, ThreadMonitor> cost,
            Map<String, ThreadMonitor> allcost) {
        StringBuffer sb = new StringBuffer("\n");
        sb.append("thd: ").append(thd).append("\n");
        int category = 0;
        for (var e0 : cost.entrySet()) {
            category += 1;
            sb.append("[").append(e0.getKey()).append("]")
                    .append("  slow_times: ").append(e0.getValue().slow_times)
                    .append(", slow_tm: ").append(e0.getValue().slow_tm).append("ms")
                    .append(", slow_max: ").append(e0.getValue().slow_max).append("ms")
                    .append(", accum_tm: ").append(e0.getValue().accum_tm).append("ms")
                    .append(", accum_times: ").append(e0.getValue().accum_times)
                    .append(", accum_wait: ").append(e0.getValue().accum_wait).append("ms")
                    .append(", wait_times: ").append(e0.getValue().wait_times)
                    .append(", wait_tm: ").append(e0.getValue().wait_tm).append("ms")
                    .append(", wait_max: ").append(e0.getValue().wait_max).append("ms")
                    .append(", wait_tasks: ").append(e0.getValue().wait_tasks)
                    .append("\n");
            if (allcost != null) {
                report(false, 0, null, e0.getKey(), e0.getValue().slow_times, e0.getValue().slow_tm, -1, -1, -1, -1,
                        allcost);
                report(false, 1, null, e0.getKey(), -1, e0.getValue().slow_max, -1, -1, -1, -1, allcost);
                report(true, 0, null, e0.getKey(), e0.getValue().accum_times, e0.getValue().accum_tm,
                        -1,
                        e0.getValue().wait_tm,
                        e0.getValue().wait_times,
                        e0.getValue().accum_wait,
                        allcost);
                report(true, 1, null, e0.getKey(), -1, -1,
                        e0.getValue().wait_tasks,
                        e0.getValue().wait_max,
                        -1, -1,
                        allcost);
            }
        }
        sb.append("category: ").append(category).append("\n");
        return sb;
    }
}
