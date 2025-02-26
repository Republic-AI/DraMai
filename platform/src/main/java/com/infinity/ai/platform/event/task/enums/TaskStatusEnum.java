package com.infinity.ai.platform.event.task.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/***
 * 状态：1:待领取, 2:任务进行中, 3:初始化, 4:领取完成
 */
@Getter
public enum TaskStatusEnum {
    INIT(0, "初始化"),
    INPROGRESS(1, "任务进行中"),
    PENDING(2, "待领取"),
    FINISH(3, "领取完成");

    private final int code;
    private final String name;

    TaskStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<TaskStatusEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }
}
