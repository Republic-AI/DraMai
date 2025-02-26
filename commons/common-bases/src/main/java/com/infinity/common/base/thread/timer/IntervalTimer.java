package com.infinity.common.base.thread.timer;


import com.infinity.common.base.thread.TimerListener;

public abstract class IntervalTimer implements TimerListener {
    private long interval;// -1 无周期
    private long elapse;
    private long delayExpire;// -1无延迟
    private long maxNums;// -1不限次数
    private boolean over;

    public IntervalTimer(long interval) {
        this(-1, interval);
    }

    public IntervalTimer(long delay, long interval) {
        this(delay, interval, -1);
    }

    public IntervalTimer(long delay, long interval, long maxNums) {
        this.delayExpire = delay >= 0 ? System.currentTimeMillis() + delay : -1;
        this.interval = interval;
        this.maxNums = this.interval <= 0 ? Math.max(1, maxNums) : maxNums;
    }

    final protected boolean isOver() {
        return this.over;
    }

    final protected void forceOver() {
        this.over = true;
    }

    @Override
    final public boolean exec(int interval) {
        if (this.over) return true;
        if (this.maxNums == 0) {
            return this.over = true;
        }
        do {
            if (this.delayExpire > 0) {
                if (System.currentTimeMillis() >= this.delayExpire) {
                    this.delayExpire = -1;
                    break;
                }
                return false;
            }
            if (this.interval > 0) {
                this.elapse += interval;
                if (this.elapse < this.interval) {
                    return false;
                }
                /* this.elapse -= this.interval; */
                this.elapse = 0;
                break;
            }
        } while (false);
        if (this.maxNums > 0) {
            this.maxNums -= 1;
            if (this.maxNums == 0) {
                this.over = true;
            }
        }
        if (this.exec0(interval)) {
            this.over = true;
        }
        return this.over;
    }

    /**
     * @param interval
     * @return 执行后是否删除当前listener
     */
    public abstract boolean exec0(int interval);
}
