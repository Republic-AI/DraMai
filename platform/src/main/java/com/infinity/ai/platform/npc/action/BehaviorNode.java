package com.infinity.ai.platform.npc.action;

import com.infinity.ai.platform.npc.NPC;

// 基本节点类
public abstract class BehaviorNode {
    public abstract boolean execute(NPC npc);
}