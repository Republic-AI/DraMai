package com.infinity.ai.platform.event.task;


import com.infinity.common.config.data.TaskCfg;

/**
 * 解锁：
 * 1.功能ID开启解锁，关联FunctionOpening表ID
 * 2.玩家等级XX，开启
 */
public interface Unlock {

    /***
     * 是否满足任务解锁
     * @param playerId 用户ID
     * @return
     */
    boolean isUnlock(long playerId, TaskCfg taskConfig);

    /***
     * 解锁当前用户满足条件的任务
     * @param playerId 用户ID
     * @param taskConfig 配置
     * @return
     */
    int unlock(long playerId, int unlockValue, TaskCfg taskConfig);
}
