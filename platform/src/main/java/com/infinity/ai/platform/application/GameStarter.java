package com.infinity.ai.platform.application;

import com.infinity.ai.platform.PlatformService;
import com.infinity.ai.platform.event.ActionService;
import com.infinity.ai.platform.event.task.PlayerTaskContext;
import com.infinity.ai.platform.manager.AIManager;
import com.infinity.ai.platform.manager.NFTManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.NpcStarter;
import com.infinity.common.base.game.IGameStarter;
import com.infinity.common.base.thread.MultiParam;
import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.db.db.DBManager;
import com.infinity.manager.node.NodeManager;
import com.infinity.manager.task.TaskManager;
import com.infinity.network.ManagerService;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import com.infinity.network.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
public final class GameStarter implements IGameStarter {
    private ApplicationContext context = null;
    private static GameStarter kInstance = null;
    private TcpServer server_ = null;

    private GameStarter() {

    }

    public static GameStarter getInstance() {
        if (kInstance == null)
            kInstance = new GameStarter();
        return kInstance;
    }

    public final void setAppContext(ApplicationContext appContext) {
        if (context == null)
            context = appContext;
    }

    @Override
    public final void start() {
        log.info(PlatformService.class.toString());
        log.info("=========================================================");
        log.info("start platform service. version={}", Version.version);

        Config config = Config.getInstance();
        Threads.regist(new MultiParam<NameParam>()
                        .append(new NameParam(ThreadConst.TIMER_1S, 4, 1000))
                        .append(new NameParam(ThreadConst.QUEUE_LOGIC, 4))
                        .append(new NameParam(ThreadConst.TIMER_50MS, 4, 50))
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

        PlayerTaskContext.init();
        DBservice.init(context);
        GameStartInit.init();
        ActionService.init();
        MessageSender.getInstance().setMyNode(config.getNodeConfig());
        RoomManager.getInstance().init();
        NpcStarter.getInstance().initializeNpc();
        AIManager.startAIChat();
        NFTManager.getInstance().init();
    }

    @Override
    public final void run() {
        Config config = Config.getInstance();

        String tcpAddress = config.getIp();
        int tcpPort = config.getPort();

        log.info("platform service listen on {}:{}", tcpAddress, tcpPort);

        final var listenerAddress = "0.0.0.0";
        server_ = new TcpServer(listenerAddress, tcpPort);
        server_.start();

        log.info("start platform service done. version={}", Version.version);
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
        //WorkChecker.getInstance().start();
        addShutdownHook();
        log.info("platform service register to cluster node done. start tcp server.");
    }

    private boolean exiting = false;

    @Override
    public void exit(String reason) {
        exiting = true;
        SpringApplication.exit(context, () -> {
            release(reason);
            return 0;
        });
    }

    private void release(String reason) {
        log.info("platform service exit...");
        DBManager.close();
        Threads.dispose();
        ManagerService.fini();
        GameConfigManager.getInstance().dispose();
        RequestIDManager.getInstance().dispose();
        log.info("platform service exit done[{}]", reason);
    }

    private void addShutdownHook() {
        // 注册一个关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> release("")));
    }
}
