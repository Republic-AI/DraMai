package com.infinity.common.base.thread;

@FunctionalInterface
public interface TimerListener {
    /**
     * @param interval
     * @return 执行后是否删除当前listener
     */
    boolean exec(int interval);
}
