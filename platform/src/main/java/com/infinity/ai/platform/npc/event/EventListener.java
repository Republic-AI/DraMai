package com.infinity.ai.platform.npc.event;

import java.util.Set;

public interface EventListener {
    void onEvent(Event event);

    Set<EventType> register();
}