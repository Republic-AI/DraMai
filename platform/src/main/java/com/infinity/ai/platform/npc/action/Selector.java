package com.infinity.ai.platform.npc.action;

import com.infinity.ai.platform.npc.NPC;

import java.util.ArrayList;
import java.util.List;

// 1.选择节点（Selector）：依次尝试子节点，直到一个成功
public class Selector extends BehaviorNode {
    private List<BehaviorNode> children = new ArrayList<>();

    public void addChild(BehaviorNode child) {
        children.add(child);
    }

    @Override
    public boolean execute(NPC npc) {
        for (BehaviorNode child : children) {
            if (child.execute(npc)) {
                return true;
            }
        }
        return false;
    }
}