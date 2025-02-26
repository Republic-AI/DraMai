package com.infinity.ai.platform.event;

import java.util.List;

/**
 * 任务服务类
 */
public class ActionService {
    private static ActionContext actionContext = null;

    public static void init() {
        if (actionContext == null) {
            actionContext = new ActionContext();
        }
    }

    public static void push(ActionParams params) {
        actionContext.push(params);
    }

    public static void push(Long playerId, ActionTypeEnum typeEnum, Object... taskPrams) {
        actionContext.push(new ActionParams(playerId, typeEnum, taskPrams));
    }

    public static void push(ActionParams... params) {
        for (ActionParams param : params) {
            actionContext.push(param);
        }
    }

    public static void push(List<ActionParams> params) {
        for (ActionParams param : params) {
            actionContext.push(param);
        }
    }
}
