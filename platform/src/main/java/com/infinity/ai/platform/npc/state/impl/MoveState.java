package com.infinity.ai.platform.npc.state.impl;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;

public class MoveState extends BaseState {

    @Override
    public void enter(NPC npc) {

    }

    @Override
    public ActionEnumType getStateType() {
        return ActionEnumType.Sleep;
    }
}
