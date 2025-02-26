package com.infinity.ai.platform.npc.state.impl;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.ai.platform.npc.state.State;

//返回到基本状态
public class ReturnToBaseState implements State {
    @Override
    public void enter(NPC npc) {
        System.out.println(npc.getName() + " returns to base.");
    }

    @Override
    public void execute(NPC npc) {
        System.out.println(npc.getName() + " is at the base.");

    }

    @Override
    public void exit(NPC npc) {
        System.out.println(npc.getName() + " leaves the base.");
    }

    @Override
    public ActionEnumType getStateType() {
        return null;
    }
}