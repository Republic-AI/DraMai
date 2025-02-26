package com.infinity.common.consts;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 登录类型
 */
public enum LoginType {

    /**
     * 0. 游客登录
     * 1. 邮箱
     * 2. 用户名称和密码登录
     * 3. 用户名称和验证码登录
     */
    GUEST(0),
    EMAIL(1),
    PLAYER_NAME_PASSWORD(2),
    PLAYER_NAME_CODE(3),
    ;

    public Integer value;

    LoginType(Integer value) {
        this.value = value;
    }

    public static Optional<LoginType> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.value, code)).findFirst();
    }
}