package com.infinity.ai.domain.tables;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//时间信息
@Data
public class PlayerTimes {
    // 下次每日重置的 时间戳,int
    private long nextResetDay;
    // 下次每周重置的 时间戳,int
    private long nextResetWeek;
    // 下次每月重置的 时间戳,int
    private long nextResetMonth;
}
