package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 围观圣诞树行为
@Slf4j
public class DrawAction extends CommonAction {

    public DrawAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public DrawAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Draw;
    }
}


