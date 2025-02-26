package com.infinity.ai.telegram.command;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

//@Component
@Slf4j
public class BotCmdContext {
    private Map<String, BotCmd> cmdMap = new HashMap<>();

    public void register(String cmd, BotCmd botCmd){
        cmdMap.putIfAbsent(cmd, botCmd);
    }

    public BotCmd getBotCmd(String cmd){
        return cmdMap.get(cmd);
    }
}
