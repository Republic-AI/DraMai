package com.infinity.common.msg.platform.room;

import lombok.Data;

@Data
public class RoomItemData {
    private String id;

    private int gridX;

    private int gridY;

    private int itemId;

    private long startTime;

    private long endTime;
}
