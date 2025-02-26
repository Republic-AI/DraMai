package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.entity.NpcChatData;
import com.infinity.ai.platform.entity.NpcSpeakData;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.repository.NpcChatDataRepository;
import com.infinity.ai.platform.repository.NpcSpeakDataRepository;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcChatHistoryRequest;
import com.infinity.common.msg.platform.npc.NpcChatHistoryResponse;
import com.infinity.common.msg.platform.npc.NpcChatVo;
import com.infinity.common.msg.platform.npc.NpcSpeakVo;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;


/**
 * 玩家登录后查询玩家NPC说话记录
 * from:H5客户端
 * target:platform
 */
@Slf4j
public class QueryNpcChatTask extends BaseTask<NpcChatHistoryRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.CHAT_HISTORY_COMMAND;
    }

    @Override
    public boolean run0() {
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("SignTask error, playerId params error,playerId={}", playerId);
            return false;
        }

        //todo 校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return false;
        }
        NpcChatHistoryRequest msg = this.getMsg();

        int type = msg.getData().getType();
        int page = msg.getData().getPage();
        int size = msg.getData().getPageSize();
        BaseMsg baseMsg;
        if (type == 1) {
            NpcSpeakDataRepository chatDataRepository =  SpringContextHolder.getBean(NpcSpeakDataRepository.class);
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
            Page<NpcSpeakData> npcSpeakDataPage = chatDataRepository.findAll(pageable);
            baseMsg = buildResponse(player, msg, npcSpeakDataPage.getContent(), null);
        } else {
            NpcChatDataRepository chatDataRepository =  SpringContextHolder.getBean(NpcChatDataRepository.class);
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
            Page<NpcChatData> npcChatDataPage = chatDataRepository.findAll(pageable);
            baseMsg = buildResponse(player, msg, null, npcChatDataPage.getContent());
        }
        sendMessage(baseMsg);
        return false;
    }

    private BaseMsg buildResponse(Player player, NpcChatHistoryRequest msg, List<NpcSpeakData> speakDataList, List<NpcChatData> chatDataList) {
        NpcChatHistoryResponse response = new NpcChatHistoryResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        NpcChatHistoryResponse.ResponseData data = new NpcChatHistoryResponse.ResponseData();
        if (speakDataList != null) {
            List<NpcSpeakVo> speakVos = new ArrayList<>();
            for (NpcSpeakData npcSpeakData : speakDataList) {
                speakVos.add(new NpcSpeakVo(npcSpeakData.getSender(), npcSpeakData.getReceiver(), npcSpeakData.getContent(), npcSpeakData.getGameTime(), npcSpeakData.getCreatedAt()));
            }
            data.setSpeakDataList(speakVos);
        } else {
            List<NpcChatVo> speakVos = new ArrayList<>();
            for (NpcChatData npcChatData : chatDataList) {
                speakVos.add(new NpcChatVo(npcChatData.getSpeakDataList(), npcChatData.getGameTime(), npcChatData.getCreatedAt()));
            }
            data.setChatDataList(speakVos);
        }
        data.setType(msg.getData().getType());
        data.setPage(msg.getData().getPage());
        data.setPageSize(msg.getData().getPageSize());
        data.setGameTime(MapDataManager.getInstance().getGameTime());
        response.setData(data);
        return response;
    }

}
