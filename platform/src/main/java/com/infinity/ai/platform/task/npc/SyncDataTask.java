package com.infinity.ai.platform.task.npc;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.SyncNpcActionRequest;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端同步状态数据给服务器
 * from: 客户端
 * dest: 服务端
 */
@Slf4j
public class SyncDataTask extends BaseTask<SyncNpcActionRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.SYNC_NPC_ACTION_COMMAND;
    }

    @Override
    public boolean run0() {
        SyncNpcActionRequest msg = this.getMsg();
        SyncNpcActionRequest.RequestData params = msg.getData();
        log.debug("SyncDataTask msg={}", msg.toString());

        //同步NPC坐标
        long npcId = params.getNpcId();
        if (npcId <= 0) {
            log.warn("SyncDataTask fail, npcId[{}] formatte error", npcId);
            return false;
        }

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
        NPC npc = (npcHolder == null) ? null : npcHolder.getNpc();
        if (npc == null) {
            log.debug("SyncDataTask fail, npc not exists,npcId={}", npcId);
            return false;
        }

        long bid = params.getBid();
        ActionData actionData = npc.getNpcModel().get_v().getAction().getBehavior().get(bid);
        if (actionData == null) {
            actionData = npc.getNpcModel().get_v().getAction().getLastAction();
            if (actionData == null || actionData.getId() == null || bid != actionData.getId().longValue()) {
                log.debug("not found action, bid={}", bid);
                return false;
            }
        }

        if (actionData != null && actionData.getStatus() != 0) {
            log.debug("action done, bid={}", bid);
            return false;
        }

        //同步物品状态
        MapDataManager.getInstance().update(params.getObjId(), params.getState(), false);

        //更新npc位置
        npc.updatePosition(params.getX(), params.getY());

        //移除当前NPC行为
        int isFinish = msg.getData().getIsFinish();
        if (isFinish == 1) {
            //npcHolder.getNpc().finishAction(params.getBid(), params.getParams());
        }

        return true;
    }
}
