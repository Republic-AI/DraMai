package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 钓鱼行动类
@Slf4j
public class CheckAction extends CommonAction {

    public CheckAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public CheckAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Check;
    }
}


