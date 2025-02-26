package com.infinity.ai.platform.npc.event;

// 玩家接近事件
public class PlayerApproachEvent implements Event {
    private String playerName;
    /*private Position playerPosition;

    public PlayerApproachEvent(String playerName, Position playerPosition) {
        this.playerName = playerName;
        this.playerPosition = playerPosition;
    }*/

    @Override
    public EventType getType() {
        return EventType.NPC_APPROACH;
    }
}