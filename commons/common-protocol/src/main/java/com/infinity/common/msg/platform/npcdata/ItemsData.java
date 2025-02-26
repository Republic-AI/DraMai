package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemsData {
    //物品名称
    public String name;
    //物品ID
    public Integer itemId;
    //物品数量
    public Integer count;
}

/*
"items": [   //拥有的物品
    {
    "name": "物品名称",
    "itemId": 111111,
    "count": 1
    }
],
*/