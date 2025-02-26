package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellingData {
    public String name;
    public Integer itemId;
    public Integer count;
}

/*
 "selling": [ //售卖物品 (如果有)
     {
     "name": "物品名称",
     "itemId": 111111,
     "itemId": 1
     }
 ],

 **/