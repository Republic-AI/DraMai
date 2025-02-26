package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedingData extends BaseData {
    //喂养的对象名称
    private String oid;

    //喂养消耗的物品
    private List<ItemData> items;
}
