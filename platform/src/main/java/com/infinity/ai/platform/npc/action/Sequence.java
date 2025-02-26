package com.infinity.ai.platform.npc.action;

import com.infinity.ai.platform.npc.NPC;

import java.util.ArrayList;
import java.util.List;

// 2.序列节点（Sequence）：依次执行子节点，直到一个失败
public class Sequence extends BehaviorNode {
    private List<BehaviorNode> children = new ArrayList<>();

    public void addChild(BehaviorNode child) {
        children.add(child);
    }

    @Override
    public boolean execute(NPC npc) {
        for (BehaviorNode child : children) {
            if (!child.execute(npc)) {
                return false;
            }
        }
        return true;
    }
}