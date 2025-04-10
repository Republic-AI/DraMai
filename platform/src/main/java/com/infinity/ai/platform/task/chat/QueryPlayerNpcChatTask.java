package com.infinity.ai.platform.task.chat;

import com.infinity.ai.platform.entity.NpcPlayerChatData;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.repository.NpcPlayerChatDataRepository;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.chat.PlayerNpcChatVo;
import com.infinity.common.msg.platform.chat.QueryNpcPlayerRequest;
import com.infinity.common.msg.platform.chat.QueryNpcPlayerResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QueryPlayerNpcChatTask extends BaseTask<QueryNpcPlayerRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_PLAYER_NPC_CHAT_COMMAND;
    }

    @Override
    public boolean run0() {
        if (playerId <= 0) {
            log.debug("QueryPlayerNpcChatTask playerId is error,request msg={}", msg);
            return false;
        }
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return true;
        }
        QueryNpcPlayerResponse queryNpcPlayerResponse = new QueryNpcPlayerResponse();
        QueryNpcPlayerResponse.ReponseData reponseData = new QueryNpcPlayerResponse.ReponseData();
        NpcPlayerChatDataRepository npcPlayerChatDataRepository = SpringContextHolder.getBean(NpcPlayerChatDataRepository.class);
        Map<Long, List<PlayerNpcChatVo>> chatMap = new HashMap<>();
        long now = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(0, 20);
        for (NpcHolder holder : NpcManager.getInstance().getOnlineNpcMap().values()) {
            List<NpcPlayerChatData> list = npcPlayerChatDataRepository.findByNpcIdAndPlayerId(holder.getNpcId(), playerId, pageable);
            List<PlayerNpcChatVo> playerNpcChatVos = chatMap.getOrDefault(holder.getNpcId(), new ArrayList<>());
            for (NpcPlayerChatData npcPlayerChatData : list) {
                PlayerNpcChatVo playerNpcChatVo = new PlayerNpcChatVo();
                playerNpcChatVo.setContent(npcPlayerChatData.getContent());
                playerNpcChatVo.setNpcSend(npcPlayerChatData.isNpcSend());
                playerNpcChatVo.setTime(now - npcPlayerChatData.getCreatedAt().getTime());
                playerNpcChatVos.add(playerNpcChatVo);
            }
            chatMap.put(holder.getNpcId(), playerNpcChatVos);
        }
        reponseData.setPlayerNpcChatDataMap(chatMap);
        queryNpcPlayerResponse.setData(reponseData);
        queryNpcPlayerResponse.setPlayerId(playerId);
        sendMessage(queryNpcPlayerResponse);
        return false;
    }
}
