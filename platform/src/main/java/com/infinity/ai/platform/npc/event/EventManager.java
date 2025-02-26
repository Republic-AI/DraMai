package com.infinity.ai.platform.npc.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {
    private Map<Integer, Set<EventListener>> listeners = new HashMap<>();

    public void subscribe(EventListener listener) {
        Set<EventType> eventTypes = null;
        if (listener == null || (eventTypes = listener.register()) == null) {
            return;
        }

        for (EventType type : eventTypes) {
            if (!listeners.containsKey(type.getCode())) {
                Set<EventListener> listenerSet = new HashSet<>();
                listenerSet.add(listener);
                listeners.putIfAbsent(type.getCode(), listenerSet);
            } else {
                listeners.get(type.getCode()).add(listener);
            }
        }
    }

    public void unsubscribe(EventListener listener) {
        Set<EventType> type = null;
        if (listener == null && (type = listener.register()) == null) {
            return;
        }

        type.stream().forEach(d -> {
            if (listeners.containsKey(d.getCode())) {
                listeners.get(d.getCode()).remove(listener);
            }
        });
    }

    public void notify(Event event) {
        Set<EventListener> eventListeners = listeners.get(event.getType().getCode());
        if (eventListeners == null || eventListeners.size() == 0) {
            return;
        }

        for (EventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }
}