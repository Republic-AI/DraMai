package com.infinity.ai.platform.manager;


import com.infinity.ai.platform.application.Config;
import com.infinity.ai.platform.common.deepseek.DeepSeekClient;
import com.infinity.ai.platform.common.deepseek.DeepSeekRequest;
import com.infinity.ai.platform.common.deepseek.DeepSeekResponse;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.msg.chat.ChatRequest;
import com.infinity.common.utils.GsonUtil;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class AIManager {

    private final static String DEEPSEEK_KEY = "sk-88cec967e1514a79b114f1022a410322";

    private final static String DEEPSEEK_URL = "https://api.deepseek.com/chat/completions";

    private static String prompt = "";

    public static void startAIChat() {
        try {
            prompt = FileUtils.readFileToString(FileUtils.getFile(Config.getInstance().getGameDataPath() + "/prompt.txt"), "UTF-8");
        } catch (Exception e) {
            log.error("read prompt.txt error", e);
            return;
        }
        Threads.addListener(ThreadConst.TIMER_1S, 0,"ai#chat", new IntervalTimer(1800000 , 1800000 ) {
            @Override
            public boolean exec0(int interval) {
                /*Player player = PlayerManager.getInstance().getOnlinePlayer();
                if (player == null) {
                    return false;
                }
                PlayerManager.getInstance().getOnlinePlayerWithID(player.getPlayerID());*/
                GameUser gameUser = GameUserMgr.getGameUser();
                if (gameUser == null) {
                    return false;
                }
                String content = callDeepSeek(prompt, "you are a chat bot, you need to give one different complete comment message every time less then 30 chars").replace("\"", "");
                ChatRequest chatRequest = new ChatRequest();
                ChatRequest.RequestData requestData = new ChatRequest.RequestData();
                requestData.setContext(content);
                requestData.setSName(gameUser.getNickName());
                requestData.setSender("chatBot");
                chatRequest.setData(requestData);
                MessageSender.getInstance().sendMessage(NodeConstant.kChatService, chatRequest);
                return false;
            }
        });
    }

    public static void main(String[] args) {
       String message =  callDeepSeek("give a message", null);
        System.out.println(message);
    }

    public static String callDeepSeek(String input, String prompt) {
        DeepSeekClient deepSeekClient = new DeepSeekClient();
        DeepSeekRequest deepSeekRequest = new DeepSeekRequest();
        deepSeekRequest.setModel("deepseek-chat");
        deepSeekRequest.setMax_tokens(30);
        deepSeekRequest.setPresence_penalty(2.0);
        List<DeepSeekRequest.Data> list = new ArrayList<>();
        DeepSeekRequest.Data systemRole = new DeepSeekRequest.Data();
        systemRole.setRole("system");
        systemRole.setContent(prompt);
        list.add(systemRole);
        DeepSeekRequest.Data useRole = new DeepSeekRequest.Data();
        useRole.setRole("user");
        useRole.setContent(input);
        list.add(useRole);
        deepSeekRequest.setMessages(list);
        DeepSeekResponse response = deepSeekClient.callDeepSeekWithHeaders(DEEPSEEK_URL, GsonUtil.parseObject(deepSeekRequest), DEEPSEEK_KEY);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
