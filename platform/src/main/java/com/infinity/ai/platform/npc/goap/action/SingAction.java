package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 唱歌行动类
@Slf4j
public class SingAction extends CommonAction {

    public SingAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public SingAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Sing;
    }
}


