package com.infinity.ai.platform.task.frame;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.sys.BroadcastRequest;
import com.infinity.common.msg.platform.npc.FrameSyncRequest;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * 位置同步
 */
@Slf4j
public class FrameSyncTask extends BaseTask<FrameSyncRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.FRAME_SYNC_COMMAND;
    }

    @Override
    public boolean run0() {
        FrameSyncRequest msg = this.getMsg();
        log.debug("frame sync,msg={}", msg.toString());

        //同步给客户端
        //frameSync(msg);

        //保存坐标
        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(msg.getData().getNpcId());
        PNpc pNpc = npcHolder.getNpcModel();
        pNpc.setX(msg.getData().getX());
        pNpc.setY(msg.getData().getY());
        return true;
    }

    private void frameSync(FrameSyncRequest msg){
        //同步连接到其他网关的客户
        FrameSyncRequest data = new FrameSyncRequest();
        data.setRequestId(msg.getRequestId());
        data.setData(msg.getData());

        BroadcastRequest response = new BroadcastRequest();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(playerId);
        response.setData(data.toString());
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
    }
}
