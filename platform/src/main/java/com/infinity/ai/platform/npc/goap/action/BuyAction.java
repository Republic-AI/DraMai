package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.BuyData;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//购买行为
@Slf4j
public class BuyAction extends Action<NpcActionRequest.BuyData> {

    public BuyAction(List<Action> preActions) {
        super(preActions);
    }

    public BuyAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public BuyAction(List<Action> preActions, List<String> oids, Map<String, Object> params) {
        super(preActions, oids, params);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.BuyData params) {
        //Long sellerNpcId = params.getNpcId().longValue();
        return BuyData.builder().oid(params.getOid()).build().toJsonString();
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Buy;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.BuyData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.BuyData params) {
        log.debug("BuyAction perform,npcId={}", npc.getId());
    }

    @Override
    public void beforePerform(NPC npc, ActionData data) {
        /*NpcActionRequest.BuyData params = getParamData(data);
        NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        if (sellerNpc == null) {
            throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
        }
        //让卖家走到指定地点
        NPC seller = sellerNpc.getNpc();
        Map<String, Object> par = new HashMap<>();
        par.put("npcId", npc.getId());
        seller.doAction(ActionEnumType.Sale.getCode(), par, data.isServerAction());*/
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.BuyData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.BuyData params) {
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid(), npc.getRoomId());
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

    @Override
    public boolean performCheck(NPC npc, ActionData actionData, NpcActionRequest.BuyData params) {
       /*NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        if (sellerNpc == null) {
            throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
        }
        NPC seller = sellerNpc.getNpc();
        if (seller.getCurActionType() != ActionEnumType.Sale.getCode()){
            return false;
        }
        return true;*/
        return true;
    }

    @Override
    public String getOid(Map<String, Object> params) {
        String npcId = String.valueOf( params.get("npcId"));
        if (npcId != null && this.params != null) {
            return (String) this.params.get(npcId);
        }
        return (String) params.get("oid");
    }
}
