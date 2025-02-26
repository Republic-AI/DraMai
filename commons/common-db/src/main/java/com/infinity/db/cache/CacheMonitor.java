package com.infinity.db.cache;

import com.infinity.db.util.FixedPriorityQueue;
import com.infinity.db.util.ObjUtils;
import com.infinity.db.util.Pair;
import com.infinity.db.util.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class CacheMonitor {
    Map<Class<?>, Pair<Long, Long>> sz_max_history_1 = new HashMap<>();
    Map<Class<?>, Pair<Long, Long>> sz_max_history = new HashMap<>();
    Map<Class<?>, Pair<Long, Long>> invalid_max_history = new HashMap<>();
    Pair<Long, Long> total_sz_max_history = new Pair<>(0L, 0L);
    Pair<Long, Long> total_invalid_max_history = new Pair<>(0L, 0L);
    Map<Class<?>, Tuple<Long, Long, Long>> orphan_history = new HashMap<>();

    private CacheMonitor() {

    }

    static CacheMonitor inst = new CacheMonitor();

    private static long printCostTm = -1;
    private static int PRINT_MIN = 1;
    private static long monitorTm = -1;
    static boolean monitoring = false;

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
        log.info("cache monitor [on]");
    }

    private static boolean monitorOff(long now) {
        if (!monitoring)
            return true;
        if (now == -1 || (monitorTm != -1 && now >= monitorTm)) {
            monitoring = false;
            inst.sz_max_history.clear();
            inst.sz_max_history_1.clear();
            inst.total_sz_max_history = new Pair<>(0L, 0L);
            inst.invalid_max_history.clear();
            inst.total_invalid_max_history = new Pair<>(0L, 0L);
            inst.orphan_history.clear();
            log.info("cache monitor [off]");
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

    static void printStats(Map<Class<?>, Pair<Long, Integer>> tszs, Map<Class<?>, Pair<Long, Integer>> vszs,
                           Map<Class<?>, FixedPriorityQueue<Pair<Long, Integer>>> vszs_sz_top10,
                           Map<Class<?>, FixedPriorityQueue<Pair<Long, Integer>>> vszs_live_top10, Pair<Long, Integer> a_tsz,
                           Pair<Long, Integer> a_vsz) {
        if (!canPrint(System.currentTimeMillis()))
            return;

        StringBuffer sb = new StringBuffer();
        sb.append("\n***************cache stat start**************").append("\n");
        for (var entry : tszs.entrySet()) {
            sb.append(entry.getKey().getSimpleName()).append(":\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "sz",
                    entry.getValue().first, null,
                    vszs.get(entry.getKey()).first, null)).append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "num",
                    entry.getValue().second, "",
                    vszs.get(entry.getKey()).second.longValue(),
                    "")).append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "sz_top10", "id", "sz"))
                    .append("\n");
            Iterator<Pair<Long, Integer>> it = vszs_sz_top10.get(entry.getKey()).iterator();
            int idx = 0;
            while (it.hasNext()) {
                var tmp = it.next();
                sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length() + "sz_top10".length(),
                        "" + (idx++), tmp.first, "", tmp.second.longValue(), null))
                        .append("\n");
            }
            sb.append(
                    ObjUtils.memprints(entry.getKey().getSimpleName().length(), "live_top10", "id", "min"))
                    .append("\n");
            it = vszs_live_top10.get(entry.getKey()).iterator();
            idx = 0;
            while (it.hasNext()) {
                var tmp = it.next();
                sb.append(
                        ObjUtils.memprints(entry.getKey().getSimpleName().length() + "live_top10".length(),
                                "" + (idx++), tmp.first, "", tmp.second.longValue(), ""))
                        .append("\n");
            }
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "sz_max_history_1",
                    inst.sz_max_history_1.get(entry.getKey()).first, null,
                    inst.sz_max_history_1.get(entry.getKey()).second, ""))
                    .append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "sz_max_history",
                    inst.sz_max_history.get(entry.getKey()).first, null,
                    inst.sz_max_history.get(entry.getKey()).second, "ms"))
                    .append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "invalid_max_history",
                    inst.invalid_max_history.get(entry.getKey()).first, null,
                    inst.invalid_max_history.get(entry.getKey()).second, "ms"))
                    .append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "orphan",
                    inst.orphan_history.get(entry.getKey()).first, "", null, null))
                    .append("\n");
            sb.append(ObjUtils.memprints(entry.getKey().getSimpleName().length(), "orphan_latest",
                    inst.orphan_history.get(entry.getKey()).second, "", inst.orphan_history.get(entry.getKey()).third,
                    "ms"))
                    .append("\n");
        }
        sb.append("total:\n");
        sb.append(ObjUtils.memprints("total".length(), "sz", a_tsz.first, null, a_vsz.first, null))
                .append("\n");
        sb.append(ObjUtils.memprints("total".length(), "nums", a_tsz.second, "", a_vsz.second.longValue(),
                ""))
                .append("\n");
        sb.append(ObjUtils.memprints("total".length(), "sz_max_history", inst.total_sz_max_history.first, null,
                inst.total_sz_max_history.second, "ms"))
                .append("\n");
        sb.append(ObjUtils.memprints("total".length(), "invalid_max_history",
                inst.total_invalid_max_history.first, null,
                inst.total_invalid_max_history.second, "ms"))
                .append("\n");
        sb.append("***************cache stat end**************");
        log.info(sb.toString());
    }
}
