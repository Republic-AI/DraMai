package com.infinity.ai.platform.task.timer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.ObjectStatus;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.map.MapObjBroadRequest;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.timer.FarmingData;
import com.infinity.common.msg.timer.SubmitExpridedMessage;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 到期消息（quartz->platform)
 */
@Slf4j
public class ExpireTask extends BaseTask<SubmitExpridedMessage> {

    protected final ObjectMapper mapper;

    public ExpireTask() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_CODE_TIMER_SUBMIT;
    }

    @Override
    public boolean run0() {
        SubmitExpridedMessage msg = this.getMsg();
        log.debug("ExpireTask msg={}", msg.toString());

        //农作物成熟
        if (msg.getSmd() == ProtocolCommon.QUARTZ_FARMING_COMMAND) {
            FarmingData data = convertMapToPOJO(msg.getData(), FarmingData.class);
            if (data == null) {
                return false;
            }

            //广播消息，更改农田状态为可收割
            MapObjBroadRequest request = new MapObjBroadRequest();
            MapObjBroadRequest.RequestData requestData = new MapObjBroadRequest.RequestData();
            requestData.setOid(data.getOid());
            requestData.setState(ObjectStatus.FarmingObjType.FRUIT.code);

            request.setData(requestData);
            broadcastMessageToService(NodeConstant.kGatewayService, request);
            MapDataManager.getInstance().update(data.getOid(), ObjectStatus.FarmingObjType.FRUIT.code, true);
        } else if (msg.getSmd() == ProtocolCommon.QUARTZ_GETUP_COMMAND) {//起床
            NpcActionRequest data = convertMapToPOJO(msg.getData(), NpcActionRequest.class);
            if (data == null) {
                return false;
            }

            NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(data.getNpcId());
            if (npcHolder == null) {
                log.debug("not found on line npc,npcId={}", data.getNpcId());
                sendMessage(buildError(ResultCode.NPC_NOT_EXIST_ERROR, data));
                return false;
            }

            //执行起床行为
            npcHolder.getNpc().doAction(data.getActionId(), data.getData(), false);
        }

        return true;
    }

    public <T> T convertMapToPOJO(Object map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

    public <T> T convertToPOJO(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
