package com.infinity.ai.platform.npc.event;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

//事件类型
@Getter
public enum EventType {
    NPC_APPROACH(0, "NPC Approach"),
    TIME_CHANGE(1, "Time Change");

    private final int code;
    private final String name;

    EventType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<EventType> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }
}
