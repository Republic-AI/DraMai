package com.infinity.ai.platform.event.task.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/***
 * 任务刷新类型
 * 1.每天凌晨五点
 * 2.每周一凌晨五点
 * 3.一次性任务
 */
@Getter
public enum RefurbishTypeEnum {

    /**
     * 每天凌晨五点
     */
    DAY(1, "日任务"),

    /**
     * 每周一凌晨五点
     */
    WEEK(2, "周任务"),

    /***
     * 一次性任务
     */
    ONE(3, "一次性任务");

    private final int code;
    private final String name;

    RefurbishTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<RefurbishTypeEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

    public static boolean isDay(int code) {
        return RefurbishTypeEnum.DAY.getCode() == code;
    }

    public static boolean isWeek(int code) {
        return RefurbishTypeEnum.WEEK.getCode() == code;
    }

    public static boolean isOne(int code) {
        return RefurbishTypeEnum.ONE.getCode() == code;
    }
}
