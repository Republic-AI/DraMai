package com.infinity.ai.platform.event;

import com.infinity.ai.platform.event.event.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/***
 * 动作场景类型
 */
@Getter
public enum ActionTypeEnum {
    LOGIN(1, "登录游戏", LoginAction::new),               //1.登录游戏
    REGISTER(2, "玩家注册", RegisterAction::new),         //玩家注册
    OFFLINE(3, "玩家离线", PlayerOfflineAction::new),    //玩家离线
    TASK_FINISH(4, "完成任务", TaskFinishAction::new),    //根据完成的任务ID
    SIGN(5, "签到", SignAction::new),               //签到

    ;

    private final int code;
    private final String name;
    private final Supplier<Action> supplier;

    ActionTypeEnum(int code, String name, Supplier<Action> supplier) {
        this.code = code;
        this.name = name;
        this.supplier = supplier;
    }

    public static Optional<ActionTypeEnum> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }
}
