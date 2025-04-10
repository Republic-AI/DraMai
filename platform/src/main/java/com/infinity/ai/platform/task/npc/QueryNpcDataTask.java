package com.infinity.ai.platform.task.npc;

import com.google.protobuf.ByteString;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.data.NpcDataListener;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.platform.npc.NpcDataSyncResponse;
import com.infinity.common.msg.platform.npc.QueryNpcDataRequest;
import com.infinity.common.msg.platform.npcdata.NpcData;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.IChannel;
import com.infinity.network.RequestIDManager;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.infinity.common.msg.ProtocolCommon.QUERY_NPC_DATA_COMMAND;

/**
 * python查询NPC数据
 * from: python
 * target: platform
 */
@Slf4j
public class QueryNpcDataTask extends BaseTask<QueryNpcDataRequest> {

    @Override
    public int getCommandID() {
        return QUERY_NPC_DATA_COMMAND;
    }

    @Override
    public boolean run0() {
        for (NpcHolder npcHolder : NpcManager.getInstance().getOnlineNpcMap().values()) {
            BaseMsg baseMsg = buildResponse(npcHolder, getMsg().getData().isInit());
            baseMsg.setRequestId(RequestIDManager.getInstance().RequestID(false));
            sendMessageToPython(getChannel(), baseMsg);
        }
        return true;
    }

    private BaseMsg buildResponse(NpcHolder npcHolder, boolean init) {
        //组装数据
        NpcDataSyncResponse request = new NpcDataSyncResponse();
        NpcDataSyncResponse.RequestData data = new NpcDataSyncResponse.RequestData();

        //NPC相关数据
        List<NpcData> npcs = new ArrayList<>();
        data.setNpcs(npcs);

        NpcManager npcManager = NpcManager.getInstance();
        NPC npc = npcHolder.getNpc();
        if (init) {
            npc.clear();
        }
        npc.getNpcDataListener().notifyProperty(true);
        NpcDataListener npcDataListener = npc.getNpcDataListener();
        npcs.add(npcDataListener.getNpcData());

        //世界数据
        data.setWorld(npcManager.getWorldDataListener().getWorldData());
        //地图数据
        //data.setMapObj(npcManager.getListener().getMapItemList());
        request.setData(data);
        return request;
    }

    protected void sendMessageToPython(IChannel channel, BaseMsg msg) {
        if (channel != null && msg != null) {
            HeaderOuterClass.Header header = makeHeader(getCommandID(), msg.getCode());
            ByteBuffer byteBuffer = buildPacketBuffer(header.toByteArray(), makeMessage(msg));
            channel.write(byteBuffer);
        }
    }

    private byte[] makeMessage(BaseMsg message) {
        return MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(message.toString().getBytes()))
                .build().toByteArray();
    }
}
