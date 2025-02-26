package com.infinity.ai.domain.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlayerSignRecordEntity  {
    private int id;
    /**签到奖励,对应配置表ID**/
    private String signReward;

    /**签到时间**/
    private long signTime;

    /**签到月份**/
    private int signMonth;

    /**本次是连续第几天签到**/
    private int signDays;

    /**签到类型:1=签到,2=补签**/
    private int signType;

    /**奖励领取状态：0=不可领取,1=待领取,2=已领取**/
    private int status;
}
