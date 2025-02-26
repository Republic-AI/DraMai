package com.infinity.ai.platform.event.task.handler.unlock;

import com.infinity.ai.platform.event.task.Unlock;
import com.infinity.ai.platform.event.task.handler.TaskUnlock;
import com.infinity.common.config.data.TaskCfg;

/***
 * 默认解锁
 */
public class DefaultTaskUnlock extends TaskUnlock implements Unlock {

    public DefaultTaskUnlock() {
    }

    @Override
    public boolean isUnlock(long playerId, TaskCfg taskConfig) {
        return true;
    }

    @Override
    public int unlock(long playerId, int unlockValue, TaskCfg taskConfig) {
        return 1;
    }

}
