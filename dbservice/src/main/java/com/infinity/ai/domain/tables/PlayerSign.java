package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.PlayerSignEntity;
import com.infinity.ai.domain.model.PlayerSignRecordEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerSign {
    private Map<Integer, PlayerSignEntity> signs = new HashMap<>();//玩家签到表 K:月
    private Map<Integer, PlayerSignRecordEntity> records = new HashMap<>();//当月玩家签到记录表 k:id
}
