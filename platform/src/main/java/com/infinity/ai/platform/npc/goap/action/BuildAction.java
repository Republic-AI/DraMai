package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 钓鱼行动类
@Slf4j
public class BuildAction extends CommonAction {

    public BuildAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public BuildAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Build;
    }
}


