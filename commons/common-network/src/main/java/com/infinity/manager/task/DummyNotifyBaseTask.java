/*
package com.infinity.manager.task;

import com.infinity.common.Dummy;
import com.infinity.network.IChannel;

public final class DummyNotifyBaseTask extends NotifyBaseTask<Dummy> {
    private Runnable func;
    private long threadMark;
    private String taskName;
    @Override
    public String getTaskName(){
        return this.taskName;
    }
    @Override
    public long getThreadMark() {
        return threadMark;
    }
    @Override
    public void setChannel(IChannel socketChannel)
    {
        throw new IllegalStateException("not support");
    }

    public DummyNotifyBaseTask(String taskName,long threadMark,Runnable func) {
        this.func = func;
        this.threadMark=threadMark;
        this.taskName=taskName;
    }

    @Override
    public int getCommandID() {
        return 0;
    }

    

    @Override
    protected boolean run0() {
        this.func.run();
        return true;
    }

}
*/
