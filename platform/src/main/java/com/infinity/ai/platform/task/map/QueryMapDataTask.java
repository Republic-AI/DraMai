package com.infinity.ai.platform.task.map;

import com.infinity.ai.NMap;
import com.infinity.ai.domain.tables.MapObject;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.map.QueryMapDataRequest;
import com.infinity.common.msg.platform.map.QueryMapDataResponse;
import com.infinity.db.db.DBManager;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询地图对象信息
 * from: 客户端
 * dest: 服务器
 */
@Slf4j
public class QueryMapDataTask extends BaseTask<QueryMapDataRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_MAP_DATA_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryMapDataRequest msg = this.getMsg();

        //校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return true;
        }

        sendMessage(buildResponse(player, msg));
        return false;
    }

    private BaseMsg buildResponse(Player player, QueryMapDataRequest msg) {
        QueryMapDataResponse response = new QueryMapDataResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        NMap mapData = DBManager.get(NMap.class, NMap.MAPID());
        if (mapData != null) {
            List<QueryMapDataResponse.ResponseData> result = new ArrayList<>();
            MapObject mapObject = mapData.get_v().getMapObject();
            mapObject.getObjMap().forEach((objId, object) -> {

                QueryMapDataResponse.ResponseData data = QueryMapDataResponse.ResponseData.builder()
                        .objId(objId)
                        .state(object.getState().toString())
                        .x(object.getX())
                        .y(object.getY())
                        .params(object.getProp())
                        .build();
                result.add(data);
            });

            if (result.size() > 0) {
                response.setData(result);
            }
        }
        return response;
    }
}
