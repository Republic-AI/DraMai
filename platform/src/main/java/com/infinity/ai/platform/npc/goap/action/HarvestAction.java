package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.MapObj;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.map.object.ObjectStatus;
import com.infinity.ai.platform.map.object.prop.FarmingProp;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.HarvestData;
import com.infinity.ai.platform.npc.goap.action.data.ItemData;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

//采收行为
@Slf4j
public class HarvestAction extends Action<NpcActionRequest.HarvestData> {

    public HarvestAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.HarvestData params) {
        MapObj obj = MapDataManager.getInstance().getMap().get_v().getMapObject().getObjMap().get(params.getOid());
        if (obj != null && obj.getState() == ObjectStatus.FarmingObjType.FRUIT.code) {
            FarmingProp farmingProp = mapper.convertValue(obj.getProp(), FarmingProp.class);
            ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(farmingProp.getItemId());
            String name = cfg == null ? "" : cfg.getName();
            ItemData item = ItemData.builder().count(farmingProp.count).itemId(farmingProp.itemId).itemName(name).build();
            return HarvestData.builder().items(Arrays.asList(item)).build().toJsonString();
        }
        return null;
    }

    public HarvestAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Harvest;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.HarvestData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.HarvestData params) {
        log.debug("HarvestAction perform,npcId={}", npc.getId());
        //获取农田的坐标
        MapObject mapObject = findMapObj(params.getOid());
        //获取DB中保存的物品数据
        MapObj obj = MapDataManager.getInstance().getMap().get_v().getMapObject().getObjMap().get(params.getOid());
        if (obj != null && obj.getState() == ObjectStatus.FarmingObjType.FRUIT.code) {
            FarmingProp farmingProp = mapper.convertValue(obj.getProp(), FarmingProp.class);
            //广播给所有客户端
            sendMessage(npc, "oid", mapObject.name, "itemId", farmingProp.getItemId());

            //保存道具
            NpcBagManager bag = npc.getHolder().bag;
            bag.addGoods(farmingProp.getItemId(), farmingProp.getCount(), false, GoodsSource.FARMING);

            //更改物品
            obj.setState(ObjectStatus.FarmingObjType.FREE.code);
        }
    }

    @Override
    public void afterPerform(NPC npc, ActionData params) {
        //结束同步数据给python
        npc.getNpcDataListener().notifyProperty(false);
    }
}
