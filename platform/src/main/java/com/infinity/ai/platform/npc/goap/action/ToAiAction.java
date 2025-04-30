package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 整理货架行动类
@Slf4j
public class ToAiAction extends CommonAction {


    public ToAiAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public ToAiAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.ToAi;
    }

}


