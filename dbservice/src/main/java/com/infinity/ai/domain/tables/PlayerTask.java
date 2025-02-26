package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.PlayerTaskEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//玩家任务
@Data
public class PlayerTask {

    //key=taskId
    Map<Integer, PlayerTaskEntity> taskMap = new HashMap<>();
}
