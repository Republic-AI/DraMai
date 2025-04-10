package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.platform.npcdata.ItemsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

//NPC数据
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NpcData implements Serializable {
    //NPC ID
    private Long id;
    //NPC名字
    private String name;
    //NPC类型
    private int type;
    //模型ID
    private int model;
    //职业
    private String career;
    //关键词
    private String keyword;
    //发型
    private int hair;
    //top
    private int top;
    //bottoms
    private int bottoms;
    //NPC移动速度
    private int speed;
    //NPC位置X
    private int x;
    //NPC位置Y
    private int y;
    //结束时间
    private long endTime;

    private String userNo;

    //当前行为
    private NpcActionBroadRequest.RequestData requestData;

    //身上的道具
    private List<ItemsData> items;

    //时装ID
    private int dressId;
    //时装剩余时间
    private int dressEndTime;
}
