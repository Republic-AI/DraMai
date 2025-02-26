package com.infinity.ai.platform.npc.data;

import com.infinity.ai.NMap;
import com.infinity.ai.domain.model.MapObj;
import com.infinity.ai.domain.tables.MapObject;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.ObjectStatus;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.common.msg.platform.npc.NpcDataSyncResponse;
import com.infinity.common.msg.platform.npcdata.MapItem;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.db.db.DBManager;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.stream.Collectors;

//属性变动事件监听器
@Slf4j
public class PropertyListener implements PropertyChangeListener {
    private Map<String, MapItem> mapObj = new HashMap<>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /*log.debug("source={}, Property changed: {},[old={}, new={}]",
                evt.getSource().getClass().getSimpleName(),
                evt.getPropertyName(),
                evt.getOldValue(), evt.getNewValue());*/

        //同步给python
        syncToPython(evt);
    }

    public void syncToPython(PropertyChangeEvent evt) {
        //地图物品数据
        /*if (mapObj == null || mapObj.size() == 0) {
            mapObject();
        }*/

        Object source = evt.getSource();
        //NPC相关数据变动
        if (source instanceof NpcDataListener) {
            try {
                //组装数据
                /*NpcDataSyncResponse request = new NpcDataSyncResponse();
                NpcDataSyncResponse.RequestData data = new NpcDataSyncResponse.RequestData();

                //NPC相关数据
                NpcDataListener npcDataListener = (NpcDataListener) source;
                List<NpcData> npcs = List.of(npcDataListener.getNpcData());
                data.setNpcs(npcs);

                //世界数据
                data.setWorld(NpcManager.getInstance().getWorldDataListener().getWorldData());
                //data.setMapObj(new ArrayList<>(this.mapObj.values()));

                request.setData(data);
                sendMessage(request);*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ((NpcDataListener) source).setSync(false);
            }
        } else if (source instanceof WorldDataListener) { //世界数据变动
            //组装数据
            NpcDataSyncResponse request = new NpcDataSyncResponse();
            NpcDataSyncResponse.RequestData data = new NpcDataSyncResponse.RequestData();

            data.setWorld(((WorldDataListener) source).getWorldData());
            request.setData(data);
            sendMessage(request);
        }
    }

    private void mapObject() {
        Map<String, com.infinity.ai.platform.map.object.MapObject> objectMap
                = MapDataManager.getInstance().getGameMap().mapData.getObjectMap();

        NMap mapData = DBManager.get(NMap.class, NMap.MAPID());
        MapObject mapObject = mapData.get_v().getMapObject();

        objectMap.values().stream().forEach(object -> {
            MapObj obj = mapObject.getObjMap().get(object.name);
            Object stateType = ObjectStatus.createState(object.type, obj == null ? 0 : obj.getState());
            String status = stateType == null ? "" : ((Enum) stateType).name();

            Set<String> availables = new HashSet();
            Enum<?>[] farmlands = ObjectStatus.getAllPossibleStates(object.type);
            if (farmlands != null && farmlands.length > 0) {
                for (int i = 0; i < farmlands.length; i++) {
                    availables.add(farmlands[i].name());
                }
            }

            MapItem mapItem = MapItem.builder()
                    .objName(object.name)
                    .oid(object.name)
                    .X(object.getX())
                    .Y(object.getY())
                    .type(object.type)
                    .status(status)
                    .availableActions(availables)
                    .build();
            mapObj.put(object.name, mapItem);
        });
    }

    public void updateItem(String oid, int state) {
        MapItem mapItem = this.mapObj.get(oid);
        if (mapItem == null) {
            return;
        }

        Object stateType = ObjectStatus.createState(mapItem.type, state);
        mapItem.setStatus(((Enum) stateType).name());
    }

    public List<MapItem> getMapItemList() {
        if (mapObj == null || mapObj.size() == 0) {
            mapObject();
        }

        return this.mapObj.values().stream().collect(Collectors.toList());
    }

    //发送数据到python服务
    public void sendMessage(NpcDataSyncResponse request) {
        request.setPlayerId(null);
        request.setGateway(null);
        request.setSessionId(null);
        request.setRequestId(RequestIDManager.getInstance().RequestID(false));
        log.debug("sendPythonMessage===================:{}",request.toString());
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPythonService, request);
    }
}