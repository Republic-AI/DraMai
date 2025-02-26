package com.infinity.ai.platform.event.task.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/***
 * 解锁类型：
 * 1.功能ID开启解锁，关联FunctionOpening表ID
 * 2.玩家等级XX，开启
 */
@Getter
public enum UnlockTypeEnum {
    DEFAULT(0, "默认"),
    OPEN_FUN(1, "功能开启"),
    LEVEL(2, "玩家等级"),
    FINISH(3, "完成任务解锁");

    private final int code;
    private final String name;

    UnlockTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<UnlockTypeEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

}
