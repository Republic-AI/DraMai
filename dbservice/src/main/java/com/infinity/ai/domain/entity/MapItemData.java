package com.infinity.ai.domain.entity;

import lombok.Data;

@Data
public class MapItemData {

    private String id;

    private int gridX;

    private int gridY;

    private int itemId;

    private long startTime;

    private long endTime;
}
