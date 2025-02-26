package com.infinity.task;


import com.infinity.manager.task.AbstractBaseTask;

/**
 * 转移到玩家线程执行
 */
public final class PlayerRebindDummyTask extends AbstractBaseTask {
    private long playerId;
    private Runnable func;
    private String taskName;

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    public PlayerRebindDummyTask(String taskName, long playerId, Runnable func) {
        this.playerId = playerId;
        this.func = func;
        this.taskName = taskName;
    }

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public int getCommandID() {
        return 0;
    }

    @Override
    public boolean run() {
        this.func.run();
        return true;
    }
}
