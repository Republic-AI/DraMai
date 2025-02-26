package com.infinity.ai.domain.model;


import io.protostuff.Exclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlayerTaskEntity implements Serializable {
    private Long id;
    private Integer taskId;//任务ID
    //private Integer type;//任务类型:0:日常任务,1:成就任务,2:竞技任务
    //private Integer subType;//任务子类型:日常任务（0主线、1日常、2周常、3支线),成就任务(0战斗、1伙伴、2装备进阶、3宠物、4竞技场、5工会),竞技(日常、周常)
    //private Integer refurbish;//刷新方式：0:一次性任务、1.每天凌晨五点、2.每周一凌晨五点
    //任务条件类型:1.竞技场胜利次数2.竞技场击败敌方英雄人数3.登录游戏4.任意卡池英雄招募次数5.ResDun关联，通关关卡ID，值为关卡的序列ID，可以填多个6.任意英雄提升等级7.体力消耗8.活跃要求
    //private Integer taskType;
    private int value1;//条件值（次数）
    private String value2;//条件值（类型）,多个以逗号隔开
    private String value3;//条件值（类型）,多个以逗号隔开
    private Integer unlock;//任务是否已解锁:0:未解锁,1:已解锁
    private Integer status;//状态：0:初始化,1:任务进行中,2:任务完成,3:领取完成

    @Exclude
    private long startTime;//开始时间
    @Exclude
    private long finishTime;//完成时间
    @Exclude
    private long awardTime;//完成时间

    @Exclude
    private Integer oldStatus;
    @Exclude
    private int oldValue1;
    @Exclude
    private String oldValue2;
    @Exclude
    private String oldValue3;
    @Exclude
    private Integer oldUnlock;

    public void saveOldValue() {
        this.oldStatus = this.status;
        this.oldValue1 = this.value1;
        this.oldValue2 = this.value2;
        this.oldValue3 = this.value3;
        this.oldUnlock = this.unlock;
    }

    public boolean isChange() {
        return (this.status != this.oldStatus
                || oldUnlock != unlock
                || (value1 != oldValue1)
                || (value2 != null && !value2.equals(oldValue2))
                || (value3 != null && !value3.equals(oldValue3))
        );
    }
}
