package com.infinity.ai.platform.npc.event;

// 时间变化事件
public class TimeChangeEvent implements Event {
    private String timeOfDay;

    public TimeChangeEvent(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public EventType getType() {
        return EventType.TIME_CHANGE;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }
}