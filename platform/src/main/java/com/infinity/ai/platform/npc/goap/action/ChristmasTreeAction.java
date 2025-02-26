package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 围观圣诞树行为
@Slf4j
public class ChristmasTreeAction extends CommonAction {

    public ChristmasTreeAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public ChristmasTreeAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.ChristmasTree;
    }
}


