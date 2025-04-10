package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.CookData;
import com.infinity.ai.platform.npc.goap.action.data.ItemData;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//做饭行为
@Slf4j
public class CookAction extends Action<NpcActionRequest.CookData> {

    public CookAction(List<Action> preActions) {
        super(preActions);
    }

    public CookAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.CookData params) {
        List<ItemData> cost = new ArrayList<>();
        List<NpcActionRequest.Item> items = params.items;
        if (items != null) {
            ItemBaseDataManager cfgManager = GameConfigManager.getInstance().getItemBaseDataManager();
            for (NpcActionRequest.Item item : items) {
                ItemCfg cfg = cfgManager.getItemConfigWithID(item.itemId);
                String name = cfg == null ? "" : cfg.getName();
                cost.add(ItemData.builder().count(item.count).itemId(item.itemId).itemName(name).build());
            }
        }

        return CookData.builder().oid(params.oid).cost(cost).build().toJsonString();
    }

    public CookAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Cook;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.CookData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.CookData params) {
        log.debug("CookAction perform,npcId={}", npc.getId());
        //获取物品的坐标
        MapObject mapObject = findMapObj(params.getOid(), npc.getRoomId());;

        //广播给所有客户端
        List<NpcActionRequest.Item> items = params.getItems() == null ? Collections.EMPTY_LIST : params.getItems();
        sendMessage(npc, "oid", mapObject.name, "items", items);

        //更新NPC背包信息，如：减少物品数量
        //NpcBagManager bag = npc.getHolder().bag;
        //items.stream().forEach(item -> bag.addGoods(item.itemId, item.count * -1, false, GoodsSource.COOK));

        //todo 同步当前行为给python
        npc.getNpcDataListener().notifyProperty(false);
    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.CookData params) {
        log.debug("TypeAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid(), npc.getRoomId());;
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.CookData params) {

    }

}
