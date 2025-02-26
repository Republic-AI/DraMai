package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.common.base.thread.ThreadMonitor;
import com.infinity.db.cache.CacheMonitor;

public class MonitorCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "monitor cache|thread MIN [PRINT_MIN=1] //监控DB缓存或线程MIN分钟，每PRINT_MIN分钟打印1次";
    }

    @Override
    protected boolean exec0(final Player owner, final String[] params) {
        String type = params[0];
        int min = Integer.parseInt(params[1]);
        int print_min = 1;
        if (params.length > 2) {
            print_min = Integer.parseInt(params[2]);
        }
        long now = System.currentTimeMillis();
        switch (type) {
            case "cache":
                CacheMonitor.monitor(now, min, print_min);
                break;
            case "thread":
                ThreadMonitor.monitor(now, min, print_min);
                break;
            default:
                return false;
        }
        return true;
    }
}