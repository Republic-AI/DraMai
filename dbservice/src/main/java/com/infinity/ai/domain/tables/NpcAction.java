package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.ActionData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NPC正在做的行为
 */
@Getter
@Setter
public class NpcAction {
    //最新的行为
    private ActionData lastAction = new ActionData();

    //正在做的行为数据：key=行为批次号
    private Map<Long, ActionData> behavior = new ConcurrentHashMap<>();
}
