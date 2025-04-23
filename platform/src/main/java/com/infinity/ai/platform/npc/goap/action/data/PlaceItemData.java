package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceItemData extends BaseData {
    private int itemId;
    private int gridX;
    private int gridY;
    private int time;
}
