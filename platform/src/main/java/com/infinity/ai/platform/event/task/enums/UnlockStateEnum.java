package com.infinity.ai.platform.event.task.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/***
 * 任务是否已解锁:0:未解锁,1:已解锁
 */
@Getter
public enum UnlockStateEnum {

    NOT_UNLOCKED(0, "未解锁"),
    UNLOCKED(1, "已解锁");

    private final int code;
    private final String name;

    UnlockStateEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<UnlockStateEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

    public static boolean isUnlocked(int code) {
        return UnlockStateEnum.UNLOCKED.getCode() == code;
    }

    public static boolean isNotUnlocked(int code) {
        return UnlockStateEnum.NOT_UNLOCKED.getCode() == code;
    }
}
