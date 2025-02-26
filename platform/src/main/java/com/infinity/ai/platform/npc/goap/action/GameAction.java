package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 打字行动类
@Slf4j
public class GameAction extends CommonAction {

    public GameAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public GameAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.DataAnalysis;
    }

}


