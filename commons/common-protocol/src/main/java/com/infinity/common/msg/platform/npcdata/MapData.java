package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapData {
    public String objName;
    public String oid;
    public String type;
    public Integer X;
    public Integer Y;
}

/*
"mapData": { //NPC自己的物品信息
        "objName": "地图物品名称",
        "oid": "物品ID",
        "type": "物品类型",
        "X": 11, //物品坐标X
        "Y": 11, //物品坐标Y
    },
 */