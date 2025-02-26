package com.infinity.ai.platform.task.gm;

import com.infinity.ai.platform.task.gm.cmd.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class GmCmds {
    private static Map<String, AbstractGmCmd> cmds = new LinkedHashMap<>();

    static {
        regist(new OnlineNumCmd());
        regist(new SetGmCmd());
        regist(new ListCmd());
        regist(new MonitorCmd());
        regist(new AddApCmd());
        regist(new AddItemCmd());
    }

    private static void regist(AbstractGmCmd cmd) {
        cmds.put(cmd.name(), cmd);
    }

    static AbstractGmCmd get(String name) {
        return cmds.get(name);
    }

    static public Map<String, AbstractGmCmd> getAll() {
        return cmds;
    }
}
