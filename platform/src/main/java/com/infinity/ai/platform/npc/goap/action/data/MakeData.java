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
public class MakeData extends BaseData {
    //在哪里制作，哪张桌子
    private String oid;

    //消耗了哪些物品
    private List<ItemData> cost;

    //生产了哪些物品
    private List<ItemData> make;
}
