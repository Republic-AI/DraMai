package com.infinity.common.consts;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

//状态：0=正常,1=封号
public enum PlayerStatus {
    /**
     * 正常
     */
    RUNNED(0, "正常"),

    /**
     * 封号
     */
    CLOSED(1, "封号");

    private final int code;
    private final String name;

    PlayerStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<PlayerStatus> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

    public int getCode() {
        return code;
    }
}
