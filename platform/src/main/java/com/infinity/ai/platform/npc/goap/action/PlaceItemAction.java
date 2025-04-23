package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.entity.MapItemData;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.tables.VMap;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.PlaceItemData;
import com.infinity.ai.platform.npc.goap.action.data.ShareData;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.platform.room.RoomItemChangeResponse;
import com.infinity.common.msg.platform.room.RoomItemData;
import com.infinity.common.utils.DateUtil;
import com.infinity.common.utils.IDGenerator;
import com.infinity.common.utils.TimeUtil;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// 放置物品
@Slf4j
public class PlaceItemAction extends Action<NpcActionRequest.PlaceItemData> {

    public PlaceItemAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.PlaceItemData params) {
        return PlaceItemData.builder().itemId(params.getItemId()).gridX(params.getGridX()).gridY(params.getGridY()).time(params.getTime()).build().toJsonString();
    }

    public PlaceItemAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.PlaceItem;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.PlaceItemData params) {
        return true;
    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.PlaceItemData params) {
        log.debug("PlaceItemAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        //sendMessage(npc, actionData, null, "npcId", String.valueOf(params.getNpcId()), "itemId", String.valueOf(params.getItemId()), "content", params.getContent());
        VMap vMap = MapDataManager.getInstance().getMap().get_v();
        List<MapItemData> list = vMap.getMapItemData().get(npc.getRoomId());
        if (list != null) {
            long now = System.currentTimeMillis();
            MapItemData mapItemData = new MapItemData();
            mapItemData.setId(IDGenerator.genId());
            mapItemData.setGridX(params.getGridX());
            mapItemData.setGridY(params.getGridY());
            mapItemData.setItemId(params.getItemId());
            mapItemData.setStartTime(now);
            mapItemData.setEndTime(now + params.getTime() * DateUtil.OneMinuteMs);
            list.add(mapItemData);

            RoomItemChangeResponse response = new RoomItemChangeResponse();
            RoomItemChangeResponse.ResponseData responseData = getResponseData(mapItemData);
            response.setData(responseData);
            for (long playerId : RoomManager.getInstance().getRoom(npc.getRoomId()).getPlayerList()) {
                response.setPlayerId(playerId);
                GameUser gameUser = GameUserMgr.getGameUser(playerId);
                log.debug("send msg -> {},{}", gameUser, response);
                if (gameUser != null) {
                    MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), response);
                }
            }
        }
    }

    private static RoomItemChangeResponse.ResponseData getResponseData(MapItemData mapItemData) {
        RoomItemChangeResponse.ResponseData responseData = new RoomItemChangeResponse.ResponseData();
        RoomItemData roomItemData = new RoomItemData();
        roomItemData.setId(mapItemData.getId());
        roomItemData.setItemId(mapItemData.getItemId());
        roomItemData.setGridX(mapItemData.getGridX());
        roomItemData.setGridY(mapItemData.getGridY());
        roomItemData.setStartTime(mapItemData.getStartTime());
        roomItemData.setEndTime(mapItemData.getStartTime());
        responseData.setRoomItemData(roomItemData);
        return responseData;
    }
}


