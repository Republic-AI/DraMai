package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.GetUpData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

//起床行动类
@Slf4j
public class GetUpAction extends Action<NpcActionRequest.GetUpData> {

    public GetUpAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    public GetUpAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.GetUp;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.GetUpData params) {
        ActionData lastAction = npc.getNpcModel().get_v().getAction().getLastAction();
        if (lastAction.getAid() != null && lastAction.getAid() != ActionEnumType.Sleep.getCode()) {
            log.debug("npc has gotten up");
            return false;
        }

        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.GetUpData params) {
        log.debug("GetUpAction perform,npcId={}", npc.getId());

        //广播睡觉行为通知
        sendMessage(npc, "oid", params.oid);
    }

    @Override
    public void afterPerform(NPC npc, ActionData params) {
        npc.getNpcDataListener().notifyProperty(false);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.GetUpData params) {
        return GetUpData.builder().oid(params.oid).build().toJsonString();
    }
}


