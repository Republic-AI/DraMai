package com.infinity.ai.platform.npc.state.impl;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.ai.platform.npc.state.State;

//定义具体的状态并使用状态机管理NPC行为。巡逻状态
public class PatrolState implements State {

    @Override
    public void enter(NPC npc) {
        System.out.println(npc.getName() + " starts patrolling.");
    }

    @Override
    public void execute(NPC npc) {
        /*npc.patrol();
        if (npc.getHealth() < 30) {
            npc.changeState(new ReturnToBaseState());
        }*/
    }

    @Override
    public void exit(NPC npc) {
        System.out.println(npc.getName() + " stops patrolling.");
    }

    @Override
    public ActionEnumType getStateType() {
        return null;
    }
}
