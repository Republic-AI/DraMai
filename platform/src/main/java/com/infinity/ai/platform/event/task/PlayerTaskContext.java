package com.infinity.ai.platform.event.task;


import com.infinity.ai.platform.event.task.handler.*;
import com.infinity.ai.platform.event.task.handler.unlock.DefaultTaskUnlock;
import com.infinity.ai.platform.event.task.handler.unlock.FinishTaskUnlock;
import com.infinity.ai.platform.event.task.handler.unlock.FunOpenUnlock;
import com.infinity.ai.platform.event.task.handler.unlock.PlayerLevelUnlock;
import com.infinity.ai.platform.event.task.enums.UnlockTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PlayerTaskContext {
    private static Map<Integer, TaskHandler> handlerMap = new HashMap<>();
    private static Map<Integer, Unlock> unlockMap = new HashMap<>();

    public static void init() {
        unlockMap.put(UnlockTypeEnum.DEFAULT.getCode(), new DefaultTaskUnlock());
        unlockMap.put(UnlockTypeEnum.OPEN_FUN.getCode(), new FunOpenUnlock());
        unlockMap.put(UnlockTypeEnum.LEVEL.getCode(), new PlayerLevelUnlock());
        unlockMap.put(UnlockTypeEnum.FINISH.getCode(), new FinishTaskUnlock());

        {
            //根据完成的任务做任务
            AbstractTaskHandler handler = new FnishTaskHandler();
            handlerMap.put(handler.getTaskType().getCode(), handler);
            //登录
            handler = new LoginHandler();
            handlerMap.put(handler.getTaskType().getCode(), handler);
            log.info("scan playerTask.handler: {}", handlerMap.size());
        }
    }

    public static Unlock getUnlock(Integer unlockType) {
        return unlockMap.get(unlockType);
    }

    public static TaskHandler getTaskHandler(Integer taskType) {
        return handlerMap.get(taskType);
    }
}
