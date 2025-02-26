package com.infinity.common.base.thread;

public class NameParam {
    private String name;// 线程组名
    private int num;// 线程数
    private int interval = -1;// N毫秒执行1次（-1表示非timer thread)
    private boolean checkdead = true;//是否检测死锁

    public NameParam(String name, int num, int interval, boolean checkdead) {
        this.name = name;
        this.num = num;
        this.interval = interval;
        this.checkdead = checkdead;
    }

    public NameParam(String name, int num, int interval) {
        this(name, num, interval, true);
    }

    public NameParam(String name, int num) {
        this(name, num, -1);
    }

    public NameParam(String name, int num, boolean checkdead) {
        this(name, num, -1, checkdead);
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isCheckdead() {
        return checkdead;
    }
}
