package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

// 移动行动类
@Slf4j
public class GotoNpcAction extends MoveToAction {

    public GotoNpcAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.GotoNpc;
    }

}


