package com.infinity.ai.platform.npc.goap;

import java.util.Map;

public class Goal {
    private String name; // 目标名称
    private int priority; // 目标优先级
    private Map<String, Boolean> conditions; // 目标条件
    private boolean completed; // 目标是否完成

    public Goal(String name, int priority, Map<String, Boolean> conditions) {
        this.name = name;
        this.priority = priority;
        this.conditions = conditions;
        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public Map<String, Boolean> getConditions() {
        return conditions;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                ", conditions=" + conditions +
                ", completed=" + completed +
                '}';
    }
}


