package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.SlaughterData;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

//屠宰行动类
@Slf4j
public class SlaughterAction extends Action<NpcActionRequest.SlaughterData> {

    private final Integer COUNT = 2;

    public SlaughterAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.SlaughterData params) {
        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(GoodsConsts.ITEM_NR_ID);
        String name = cfg == null ? "" : cfg.getName();
        return SlaughterData.builder()
                .oid(params.oid)
                .itemId(GoodsConsts.ITEM_NR_ID)
                .itemName(name)
                .count(COUNT).build().toJsonString();
    }

    public SlaughterAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Slaughter;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.SlaughterData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.SlaughterData params) {
        log.debug("SlaughterAction perform,npcId={}", npc.getId());
        //获取屠宰对象：牛的坐标
        MapObject mapObject = findMapObj(params.getOid());

        //广播给所有客户端
        sendMessage(npc, "oid", mapObject.name, "itemId", GoodsConsts.ITEM_NR_ID, "count", COUNT);

        //更新NPC背包信息，如：减少物品数量
        NpcBagManager bag = npc.getHolder().bag;
        bag.addGoods(GoodsConsts.ITEM_NR_ID, COUNT, false, GoodsSource.Slaughter);
    }

}


