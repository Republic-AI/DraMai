package com.infinity.ai.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlayerSignEntity  {
    private int totalDays;//累计签到天数
    private int continuousDays;//连续签到天数
    private int signMonth;//签到月份
    private long lastSignTime;//最后一次签到时间
    private int signNums;//可补签次数
}
