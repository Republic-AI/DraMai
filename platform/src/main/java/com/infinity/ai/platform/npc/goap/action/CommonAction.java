package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.CommonData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 打字行动类
@Slf4j
public class CommonAction extends Action<NpcActionRequest.CommonData> {

    public CommonAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public CommonAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.CommonData params) {
        return CommonData.builder().oid(params.getOid()).build().toJsonString();
    }


    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Free;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.CommonData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.CommonData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.CommonData params) {
        log.debug("TypeAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid());
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

}


