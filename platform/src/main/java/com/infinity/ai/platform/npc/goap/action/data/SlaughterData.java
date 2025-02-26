package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaughterData extends BaseData {
    //屠宰的物品名称
    private String oid;
    private Integer itemId;
    //牛肉道具ID
    private String itemName;
    //牛肉增加数量
    private Integer count;
}
