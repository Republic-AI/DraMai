package com.infinity.ai.domain.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlayerTaskReceiveEntity implements Serializable {
    private Long id;
    private long playerId;//玩家ID
    private long taskId;//任务ID
    private Integer type;//任务类型:0:日常任务,1:成就任务,2:竞技任务
    private Integer subType;//任务子类型:日常任务（0主线、1日常、2周常、3支线),成就任务(0战斗、1伙伴、2装备进阶、3宠物、4竞技场、5工会),竞技(日常、周常)
    //任务条件类型:1.竞技场胜利次数2.竞技场击败敌方英雄人数3.登录游戏4.任意卡池英雄招募次数5.ResDun关联，通关关卡ID，值为关卡的序列ID，可以填多个6.任意英雄提升等级7.体力消耗8.活跃要求
    private Integer taskType;
    private Integer refurbish;//刷新方式：0:一次性任务、1.每天凌晨五点、2.每周一凌晨五点
    private Integer goodsId; //商品ID
    private Integer goodsType; //商品类型
    private Integer count; //数量
}
