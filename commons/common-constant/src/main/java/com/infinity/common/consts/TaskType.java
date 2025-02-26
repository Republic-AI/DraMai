package com.infinity.common.consts;

import java.util.Objects;

//玩家任务
public enum TaskType {

    //默认(占位）)
    TypeDefault(-1),
    //日常任务
    EveryDay(100),
    //成就任务
    Achievement(101);

    private final int code;

    TaskType(int code) {
        this.code = code;
    }

    public static TaskType getByCode(int code) {
        for (TaskType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return TypeDefault;
    }
}