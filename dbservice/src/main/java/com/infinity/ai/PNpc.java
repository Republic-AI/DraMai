package com.infinity.ai;


import com.infinity.ai.domain.tables.VNpc;
import com.infinity.db.db.DBEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 玩家
 */
@Data
@ToString
public class PNpc implements DBEntity<VNpc> {
    //NPC ID
    private long id;
    ////NPC所属玩家,0:代表NPC，非0代表玩家的
    private long playerId;
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
    //dressId
    private int dressId;

    //NPC可产出物品清单
    /*private String itemProduct;
    //NPC可消耗物品清单
    private String itemUse;
    //巡逻路径
    private String patrolPath;
    //日常安排
    private String schedule;
    //NPC对话内容
    private String dialogue;*/

    //创建时间
    private long createdate;

    //结束时间
    private long endTime;

    //详细
    private transient byte[] v;

    private VNpc _v = new VNpc();

    public PNpc() {
        super();
    }

    @Override
    public String getTableName() {
        return "npc";
    }

    @Override
    public int getCacheExpire() {
        return 60;
    }

    @Override
    public Class<VNpc> ref_v() {
        return VNpc.class;
    }

}