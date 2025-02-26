package com.infinity.ai.chat.application;

import cn.hutool.core.io.FileUtil;
import com.infinity.ai.chat.ChatService;
import com.infinity.ai.chat.manager.ChatManager;
import com.infinity.common.base.game.IGameStarter;
import com.infinity.common.base.thread.MultiParam;
import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.core.text.CharsetKit;
import com.infinity.common.utils.TrieWordFilter;
import com.infinity.manager.node.NodeManager;
import com.infinity.manager.task.TaskManager;
import com.infinity.network.ManagerService;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import com.infinity.network.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.CharSetUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;

@Slf4j
public final class ChatStarter implements IGameStarter {
    private ApplicationContext context = null;
    private static ChatStarter kInstance = null;
    private TcpServer server_ = null;

    private ChatStarter() {

    }

    public static ChatStarter getInstance() {
        if (kInstance == null)
            kInstance = new ChatStarter();
        return kInstance;
    }

    public final void setAppContext(ApplicationContext appContext) {
        if (context == null)
            context = appContext;
    }

    @Override
    public final void start() {
        log.info(ChatService.class.toString());
        log.info("=========================================================");
        log.info("start chat Service. version={}", Version.version);

        Config config = Config.getInstance();
        Threads.regist(new MultiParam<NameParam>()
                        .append(new NameParam(ThreadConst.TIMER_1S, 1, 1000))
                        .append(new NameParam(ThreadConst.QUEUE_LOGIC, 8))
                //.append(new NameParam(ThreadConst.QUEUE_FRAG, 2))
                //.append(new NameParam(ThreadConst.QUEUE_LOG, 2))
        );

        ManagerService.init();
        log.info("init handler system.");
        NewTaskFactory newTaskFactory = new NewTaskFactory();
        ManagerService.setTaskFactory(newTaskFactory);

        TaskManager taskManager = new TaskManager(config.getTaskPoolLength());
        ManagerService.setTaskManager(taskManager);

        log.info("init node manager system.");
        NodeManager nodeMgr = NodeManager.getInstance();
        nodeMgr.init();
        nodeMgr.setMyNode(config.getNode_info_());
        ChatManager.getInstance().init();
        MessageSender.getInstance().setMyNode(config.getNodeConfig());

        log.info("init trie word filter");
        initTrieWordFilter();
    }

    @Override
    public final void run() {
        Config config = Config.getInstance();

        String tcpAddress = config.getIp();
        int tcpPort = config.getPort();

        log.info("chat service listen on {}:{}", tcpAddress, tcpPort);

        final var listenerAddress = "0.0.0.0";
        server_ = new TcpServer(listenerAddress, tcpPort);
        server_.start();

        log.info("start chat service done. version={}", Version.version);
        if (!ManagerService.getRegister().register(config.getInstance().getNodeConfig())) {
            log.info("register nodeInfo to redis fail, {}", config.getInstance().getNodeId());
            System.exit(0);
        }

        // 向节点服务器注册，注册完毕后开启服务
        log.info("start register cluster ......");
        NodeManager nodeMgr = NodeManager.getInstance();
        nodeMgr.connect();

        while (!exiting && !nodeMgr.isRegistered()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("register cluster success......");
        log.info("chat service register to cluster node done. start tcp server.");
    }

    private boolean exiting = false;

    @Override
    public void exit(String reason) {
        exiting = true;
        SpringApplication.exit(context, () ->
        {
            log.info("chat Service exit...");
            Threads.dispose();
            ManagerService.fini();
            GameConfigManager.getInstance().dispose();
            RequestIDManager.getInstance().dispose();
            log.info("chat Service exit done[{}]", reason);
            return 0;
        });
    }

    public void initTrieWordFilter() {
        String gameDataPath = Config.getInstance().getGameDataPath();
        for (String badWord : FileUtil.readLines(new File(gameDataPath + "/badWords.txt"), CharsetKit.CHARSET_UTF_8)) {
            TrieWordFilter.getInstance().addBlockedWord(badWord);
        }
    }
}
