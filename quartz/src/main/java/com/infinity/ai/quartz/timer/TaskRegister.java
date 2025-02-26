package com.infinity.ai.quartz.timer;

import com.infinity.common.utils.spring.SpringContextHolder;

import java.util.Map;

public class TaskRegister {

    public void run() {
        Map<String, ITaskRegister> beansMap = SpringContextHolder.getApplicationContext().getBeansOfType(ITaskRegister.class);
        for (Map.Entry<String, ITaskRegister> entry : beansMap.entrySet()) {
            if (entry.getValue().isNeedRegister()) {
                entry.getValue().register();
            }
        }
    }
}
