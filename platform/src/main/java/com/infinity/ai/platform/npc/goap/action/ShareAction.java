package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.ShareData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 分享行动类
@Slf4j
public class ShareAction extends Action<NpcActionRequest.ShareData> {

    public ShareAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.ShareData params) {
        return ShareData.builder().npcId(params.npcId).build().toJsonString();
    }

    public ShareAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Share;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.ShareData params) {
        return true;
    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.ShareData params) {
        log.debug("ShareAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        sendMessage(npc, actionData, null, "npcId", params.getNpcId());
    }
}


