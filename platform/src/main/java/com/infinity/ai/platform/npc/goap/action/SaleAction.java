package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.SaleData;
import com.infinity.ai.platform.npc.goap.action.data.SaleItem;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//销售行为
@Slf4j
public class SaleAction extends Action<NpcActionRequest.SaleData> {

    public SaleAction(List<Action> preActions) {
        super(preActions);
    }

    public SaleAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.SaleData params) {
        return SaleData.builder().oid(params.getOid()).build().toJsonString();
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Sale;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.SaleData params) {
        return true;
    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.SaleData params) {
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid());
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

    @Override
    public boolean performCheck(NPC npc, ActionData actionData, NpcActionRequest.SaleData params) {
        /*NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        if (sellerNpc == null) {
            throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
        }
        NPC seller = sellerNpc.getNpc();
        if (seller.getCurActionType() != ActionEnumType.Buy.getCode()){
            return false;
        }*/
        return true;
    }
}
