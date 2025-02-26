package com.infinity.ai.platform.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ActionParams implements Serializable {
    //玩家ID
    private long playerId;
    //动作场景类型
    private ActionTypeEnum typeEnum;
    //动作场景参数
    private Object[] taskPrams;

    public ActionParams(long playerId, ActionTypeEnum typeEnum, Object... taskPrams) {
        this.playerId = playerId;
        this.typeEnum = typeEnum;
        this.taskPrams = taskPrams;
    }
}
