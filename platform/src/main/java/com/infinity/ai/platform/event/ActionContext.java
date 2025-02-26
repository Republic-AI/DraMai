package com.infinity.ai.platform.event;

import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;

public class ActionContext {

    public ActionContext() {
    }

    public void push(ActionParams params) {
        Threads.runAsync(ThreadConst.QUEUE_LOGIC, params.getPlayerId(), "ActionTypeEnum#" + params.getTypeEnum().name(), () -> {
            Action action = params.getTypeEnum().getSupplier().get();
            if (action != null) {
                action.execute(params);
            }
        });
    }
}
