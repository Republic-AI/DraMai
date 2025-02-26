package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.NMap;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.MapObj;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.map.object.ObjectStatus;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.FarmingData;
import com.infinity.ai.platform.npc.goap.action.data.ItemData;
import com.infinity.ai.platform.task.timer.ExpireMsgBuilder;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.timer.SubmitExpridedMessage;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//耕种行为

/**
 * 1. NPC走到耕种区域
 * 2. 生产期间NPC头上出现气泡显示，气泡中出现相应生产图标
 * 3. NPC前方/耕种区域出现“种苗”
 * 4. 第二天""种苗""变成“蔬菜/玉米/小麦”"
 * <p>
 * "1.行为：耕种行为ID(100)
 * 2.哪个NPC执行(NPCID)
 * 3.到哪里耕种/农场坐标(where)
 * 4.耕种什么(如玉米ID、大麦ID）"
 */
@Slf4j
public class FarmingAction extends Action<NpcActionRequest.FarmingData> {
    private final Integer COUNT = 3;

    public FarmingAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    public FarmingAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.FarmingData params) {
        //todo 种植数量
        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(params.getItemId());
        String name = cfg == null ? "" : cfg.getName();
        ItemData itemData = ItemData.builder().itemId(params.itemId).count(COUNT).itemName(name).build();
        return FarmingData.builder().oid(params.oid).item(itemData).build().toJsonString();
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Farming;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.FarmingData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.FarmingData params) {
        log.debug("FarmingAction perform,npcId={}", npc.getId());
        //获取农场坐标
        MapObject mapObject = findMapObj(npc.getId().intValue(), params);

        //广播给所有客户端
        sendMessage(npc, "oid", mapObject.name, "itemId", params.getItemId());

        //更新地图物品的状态
        updateMapItem(npc, mapObject, params);

        //8小时后改变状态
        SubmitExpridedMessage msg = ExpireMsgBuilder.buildMsg(ProtocolCommon.QUARTZ_FARMING_COMMAND, MapDataManager.DAY, npc.getId());
        msg.setData(FarmingData.builder().oid(mapObject.name).build());
        log.debug("Send message to Quartz, msg={}", msg.toString());
        MessageSender.getInstance().sendMessage(NodeConstant.kQuartzService, msg);
    }


    private MapObject findMapObj(Integer npcId, NpcActionRequest.FarmingData params) {
        MapDataManager mapDataManager = MapDataManager.getInstance();
        GameMap gameMap = mapDataManager.getGameMap();
        String area = params.getOid();
        if (!StringUtils.isEmpty(area)) {
            MapObject object = gameMap.getObject(area);
            if (object != null)
                return object;
        }

        List<MapObject> objectList = gameMap.getByNpcIdType(npcId, ObjectStatus.MapObjType.Farmland.name);
        if (objectList == null || objectList.size() == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND_OBJECT_ERROR);
        }

        NMap map = mapDataManager.getMap();
        com.infinity.ai.domain.tables.MapObject dbMapObject = map.get_v().getMapObject();
        for (MapObject mapObject : objectList) {
            MapObj mapObj = dbMapObject.objMap.get(mapObject.getId());
            if (mapObj == null || mapObj.getState() == 0) {
                return mapObject;
            }
        }
        //未找到农田
        throw new BusinessException(ResultCode.NOT_FOUND_OBJECT_ERROR);
    }

    private void updateMapItem(NPC npc, MapObject mapObject, NpcActionRequest.FarmingData params) {
        //保存地图物品数据
        NMap map = MapDataManager.getInstance().getMap();
        MapObj mapObj = map.get_v().getMapObject().getObjMap().get(mapObject.getId());
        if (mapObj == null) {
            mapObj = MapObj.buildMapObj();
            mapObj.setId(mapObject.getName());
            mapObj.setX(mapObject.x);
            mapObj.setY(mapObject.y);
            mapObj.setState(0);
            mapObj.setProp(new HashMap<>());
            map.get_v().getMapObject().getObjMap().put(mapObj.getId(), mapObj);
        }

        //更新数据
        mapObj.setState(ObjectStatus.FarmingObjType.SEEDLING.code);
        mapObj.getProp().put("itemId", params.getItemId());
        mapObj.getProp().put("count", COUNT);
    }
}
