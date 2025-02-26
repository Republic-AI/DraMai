package com.infinity.db.util;
/**
 * 
 * 只用作唯一ID，不要解析ID
 */
public class Snowflake {
    // 起始的时间戳
    private final static long START_STMP = 1577808000; // 2020-01-01

    private final static int SEQUENCE_BIT = 13; // 序列号占用的位数
    private final static int MACHINE_BIT = 22; // 机器标识占用的位数
    // 每一部分最大值
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    // 每一部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private long machineId; // 机器标识
    private long sequence = 0L; // 序列号
    private long lastStmp = -1; // 上一次时间戳

    public Snowflake(int machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.machineId = machineId;
        this.lastStmp = timeGen();
    }

    // 产生下一个ID
    public synchronized long nextId() {
        sequence = (sequence + 1) & MAX_SEQUENCE;
        if (sequence == 0L) {
            lastStmp = getNextSec();
        }
        return (lastStmp - START_STMP) << TIMESTMP_LEFT // 时间戳部分
                | machineId << MACHINE_LEFT // 机器标识部分
                | sequence; // 序列号部分
    }

    private int getNextSec() {
        return (int) lastStmp + 1;
    }

    private int timeGen() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static void main(String[] args) {
        var sn = new Snowflake(0);
        for (int i = 0; i < 10; i++) {
            System.out.println(sn.nextId());
        }
    }
}
