package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 打字行动类
@Slf4j
public class WaterAction extends CommonAction {

    public WaterAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public WaterAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Water;
    }
}


