package com.infinity.ai.platform.event.task.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/***
 * 任务类型：
 * 1.日常登录游戏
 * 2.累计登录游戏
 * 3.收集猫咪图鉴
 * 4.猫德满级数量
 * 5.分享
 */
@Getter
public enum TaskTypeEnum {
    LOGIN(1, "日常登录游戏"),
    STRENGTH_COST(99, "体力消耗"),
    BY_FINISH(100, "根据完成的任务ID"),
    ;

    private final int code;
    private final String name;

    TaskTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<TaskTypeEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

}
