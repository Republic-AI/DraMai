package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.RepairData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

// 打字行动类
@Slf4j
public class RepairAction extends Action<NpcActionRequest.RepairData> {

    public RepairAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    public RepairAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.RepairData params) {
        return RepairData.builder().oid(params.getOid()).build().toJsonString();
    }

    public RepairAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Repair;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.RepairData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.RepairData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.RepairData params) {
        log.debug("TypeAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid(), npc.getRoomId());;
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

}


