package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.map.AStar;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.MoveData;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.ActionStatus;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 移动行动类
@Slf4j
public class MoveToAction extends Action<NpcActionRequest.MoveData> {

    public MoveToAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.MoveData params) {
        return MoveData.builder().npcId(params.getNpcId()).oid(params.getOid()).gridX(params.getGridX()).gridY(params.getGridY()).build().toJsonString();
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Move;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.MoveData params) {
        return true;
    }

    //移动NPC到目标位置
    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.MoveData params) {
        long now = System.currentTimeMillis();
        int curIndex = (int)actionData.getParams().getOrDefault("curIndex", 0);
        long lastMoveTime = (long)actionData.getParams().getOrDefault("lastMoveTime", 0L);
        List<Position> pathList = (List<Position>) actionData.getParams().get("path");
        if (CollectionUtils.isEmpty(pathList)) {
            actionData.setStatus(ActionStatus.FAIL);
            return;
        }
        if (curIndex >= pathList.size() - 1) {
            return;
        }
        if (now - lastMoveTime < 200) {
            return;
        }
        curIndex += 1;
        Position targetPosition = pathList.get(pathList.size() - 1);
        if (!RoomManager.getInstance().getMapManager(npc.getRoomId()).canStand(npc, targetPosition.getX(), targetPosition.getY())) {
            firstPerform(npc, actionData, params);
            actionData.getParams().put("curIndex", 0);
            return;
        }
        actionData.getParams().put("curIndex", curIndex);
        actionData.getParams().put("lastMoveTime", now);
        Position position = pathList.get(curIndex);
        npc.updatePosition(position.getX() * GameConsts.GRID_X, position.getY() * GameConsts.GRID_Y);
    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.MoveData params) {
        log.debug("MoveToAction perform,npcId={}", npc.getId());

        Position temp = getTargetPosition(npc, params);

        if (npc.getGridPostion().equals(temp)) {
            return;
        }
        GameMap gameMap = RoomManager.getInstance().getRoom(npc.getRoomId()).getGameMap();
        List<Position> pathList = gameMap.aStar.findPath(npc, npc.getGridPostion(), temp);
        if (pathList.isEmpty()) {
            log.debug("MoveToAction not found path,npcId={}, cur = {}, target = {}", npc.getId(), npc.getGridPostion(), temp);
            actionData.setStatus(ActionStatus.FAIL);
            return;
        }
        if (pathList.size() == 1) {
            return;
        }
        actionData.getParams().put("path", pathList);
        actionData.setEndTime(actionData.getStartTime() + (pathList.size() - 1) * 200L);
        actionData.setStatus(ActionStatus.DOING);
        sendMessage(npc, actionData, null, "path", pathList);
        //MapManager.getInstance().updatePostion(npc, pathList.get(pathList.size() - 1).getX(), pathList.get(pathList.size() - 1).getY());
    }

    public Position getTargetPosition(NPC npc, NpcActionRequest.MoveData params) {
        int gridX;
        int girdY;
        MapObject mapObject;
        if (!StringUtils.isEmpty(params.getOid()) && (mapObject = findMapObj(params.getOid(), npc.getRoomId())) != null) {
            if (!RoomManager.getInstance().getMapManager(npc.getRoomId()).canStand(npc, mapObject.getGridX(), mapObject.getGridY())) {
                List<Position> positionList = getEmptyNeighbors(new Position(mapObject.getGridX(), mapObject.getGridY()), npc.getRoomId());
                if (positionList == null || positionList.isEmpty()) {
                    throw new BusinessException("position not found");
                }
                gridX = positionList.get(0).x;
                girdY = positionList.get(0).y;
            } else {
                gridX = mapObject.getGridX();
                girdY = mapObject.getGridY();
            }
        } else if (params.getNpcId() != null) {
            NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
            if (sellerNpc == null) {
                throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
            }
            NPC tageNpc = sellerNpc.getNpc();
            List<Position> positionList = getEmptyNeighbors(tageNpc.getGridPostion(), npc.getRoomId());
            if (positionList == null || positionList.isEmpty()) {
                throw new BusinessException("position not found");
            }
            gridX = positionList.get(0).x;
            girdY = positionList.get(0).y;
        } else{
            gridX = params.getGridX();
            girdY = params.getGridY();
        }
        return new Position(gridX, girdY);
    }

    @Override
    public void afterPerform(NPC npc, ActionData actionData) {
        NpcActionRequest.MoveData moveData = getParamData(actionData);
        if (moveData.getNpcId() != null && !npc.getNpcModel().get_v().getTalk().isTalking()) {
            npc.getNpcDataListener().notifyProperty(true);
        }
        List<Position> pathList = (List<Position>) actionData.getParams().get("path");
        if (CollectionUtils.isEmpty(pathList)) {
            actionData.setStatus(ActionStatus.FAIL);
            return;
        }
        Position position = pathList.get(pathList.size() - 1);
        npc.updatePosition(position.getX() * GameConsts.GRID_X, position.getY() * GameConsts.GRID_Y);
    }

    @Override
    public boolean checkEnd(NPC npc, ActionData data) {
        NpcActionRequest.MoveData params = getParamData(data);
        if (!StringUtils.isEmpty(params.getOid())) {
            //获取目标对象坐标
            int curIndex = (int)data.getParams().getOrDefault("curIndex", 0);
            List<Position> pathList = (List<Position>) data.getParams().get("path");
            if (CollectionUtils.isEmpty(pathList)) {
                return true;
            }
            return curIndex == (pathList.size() - 1);
        } else if (params.getNpcId() != null) {
            List<Position> pathList = (List<Position>) data.getParams().get("path");
            int curIndex = (int)data.getParams().getOrDefault("curIndex", 0);
            NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
            if (sellerNpc == null) {
                throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
            }
            NPC tageNpc = sellerNpc.getNpc();
            return npc.distanceTo(tageNpc) <= 64 || (curIndex == pathList.size() - 1 && !tageNpc.getGridPostion().equals(pathList.get(curIndex)));
        } else {
            return npc.getGridPostion().equals(new Position(params.getGridX(), params.getGridY()));
        }
    }

    // 获取邻居节点
    public List<Position> getEmptyNeighbors(Position node, int roomId) {
        NpcRoom npcRoom = RoomManager.getInstance().getRoom(roomId);
        AStar aStar = npcRoom.getGameMap().aStar;
        List<Position> neighbors = new ArrayList<>();
        // 八个方向：上、右、下、左、左上、右上、左下、右下
        int[][] directions = {
                {0, 1},  // 上
                {1, 0},  // 右
                {0, -1}, // 下
                {-1, 0}, // 左
                {-1, 1}, // 左上
                {1, 1},  // 右上
                {1, -1}, // 右下
                {-1, -1} // 左下
        };

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            // 检查新的坐标是否在地图范围内且不是障碍物
            if (newX >= 0 && newX < aStar.getWidth() && newY >= 0 && newY < aStar.getHeight() && aStar.getMap()[newX][newY] == 0) {
                if (RoomManager.getInstance().getMapManager(roomId).isEmpty(newX, newY)) {
                    neighbors.add(new Position(newX, newY));
                }
            }
        }
        return neighbors;
    }
}


