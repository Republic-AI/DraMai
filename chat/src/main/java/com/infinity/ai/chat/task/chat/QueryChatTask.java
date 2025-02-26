package com.infinity.ai.chat.task.chat;

import com.alibaba.fastjson.JSON;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import com.infinity.common.msg.chat.QueryChatRequest;
import com.infinity.common.msg.chat.QueryChatResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天
 */
@Slf4j
public class QueryChatTask extends BaseTask<QueryChatRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.kQueryChatCommand;
    }

    @Override
    public boolean run0() {
        QueryChatRequest msg = this.getMsg();
        if (playerId <= 0) {
            log.debug("playerId Invalid parameters ,request msg={}", msg);
            return false;
        }
        QueryChatRequest.RequestData requestData = msg.getData();
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RedisKeyEnum chatKey = RedisKeyEnum.CHAT;
        RList<String> list = redissonClient.getList(chatKey.getKey());
        sendMessage(buildResponse(requestData.getNpcId(), list.readAll(), msg));
        return true;
    }

    private BaseMsg buildResponse(long npcId, List<String> list, QueryChatRequest msg) {
        QueryChatResponse response = new QueryChatResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(msg.getPlayerId());

        QueryChatResponse.ResponseData data = new QueryChatResponse.ResponseData();
        List<ChatData> dataList = new ArrayList<>();
        list.stream().forEach(chat -> dataList.add(JSON.parseObject(chat, ChatData.class)));
        data.setChats(dataList);
        data.setNpcId(npcId);
        response.setData(data);
        return response;
    }
}
