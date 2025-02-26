package com.infinity.manager.task;

import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.Threads;
import com.infinity.network.NetThreadConst;
import com.infinity.task.ITask;

public class TaskManager implements com.infinity.task.ITaskManager {

    public TaskManager(int maxThreadNumber) {
        Threads.regist(new NameParam(NetThreadConst.QUEUE_TaskThread, maxThreadNumber));
    }

    @Override
    public void add(ITask task) {
        if (task == null) {
            return;
        }

        final long threadMark = task.getThreadMark();
        final String taskName = String.format("cmd[%s,%s]", task.getHeader() == null ? "?" : task.getHeader().getCommand(), "?".equals(task.getTaskName()) ? task.getClass().getSimpleName() : task.getTaskName());
        if (threadMark == ITask.DEFAULT_THREAD_MARK) {
            Threads.runAsync(NetThreadConst.QUEUE_TaskThread, taskName, task::execute);
        } else {
            Threads.runAsync(NetThreadConst.QUEUE_TaskThread, threadMark, taskName, task::execute);
        }
    }
}

