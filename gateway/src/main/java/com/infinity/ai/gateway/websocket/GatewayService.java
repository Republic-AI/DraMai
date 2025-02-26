package com.infinity.ai.gateway.websocket;

import com.infinity.ai.gateway.application.NewTaskFactory;
import com.infinity.ai.gateway.application.Version;
import com.infinity.ai.gateway.websocket.net.WebSocketServer;
import com.infinity.common.base.thread.MultiParam;
import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.manager.node.NodeManager;
import com.infinity.manager.task.TaskManager;
import com.infinity.network.ManagerService;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import com.infinity.network.TcpServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GatewayService {
    private TcpServer server_ = null;
    private WebSocketServer server = null;

    public static void main(String[] args) {
        GatewayService service = new GatewayService();
        service.start();
        service.startWebSocket();
        service.run();

        Runtime.getRuntime().addShutdownHook(new Thread("gateway-shutdown-hook") {
            @Override
            public void run() {
                service.exit("system exists");
            }
        });
    }

    public void startWebSocket() {
        log.info("start start up gateway service websocket......");
        Thread t = new Thread(() -> {
            server = new WebSocketServer();
            try {
                server.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public final void start() {
        log.info(GatewayService.class.toString());
        log.info("=========================================================");
        log.info("start gateway service. version=%s", Version.version);

        GatewayConfig config = GatewayConfig.getInstance();

        Threads.regist(new MultiParam<NameParam>()
                        .append(new NameParam(ThreadConst.TIMER_1S, 1, 1000))
                //.append(new NameParam(ThreadConst.QUEUE_LOGIC, 4))
                //.append(new NameParam(ThreadConst.QUEUE_FRAG, 2))
                //.append(new NameParam(ThreadConst.QUEUE_LOG, 2))
        );
        ManagerService.init();

        log.info("init task system.");
        NewTaskFactory newTaskFactory = new NewTaskFactory();
        ManagerService.setTaskFactory(newTaskFactory);

        TaskManager taskManager = new TaskManager(config.getTaskPoolLength());
        ManagerService.setTaskManager(taskManager);

        log.info("init node manager system.");
        NodeManager nodeMgr = NodeManager.getInstance();
        nodeMgr.init();
        nodeMgr.setMyNode(config.getInstance().getNode_info_());
        MessageSender.getInstance().setMyNode(config.getNodeConfig());
    }

    public final void run() {
        GatewayConfig config = GatewayConfig.getInstance();
        String tcpAddress = config.getIp();
        int tcpPort = config.getPort();
        log.info("gateway service listen on {}:{}", tcpAddress, tcpPort);
        final var listenerAddress = "0.0.0.0";
        server_ = new TcpServer(listenerAddress, tcpPort);
        server_.start();

        log.info("start gateway service done. version={}", Version.version);
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
                //LoggerHelper.info("waiting connect to cluster node service......");
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("register cluster success......");
        log.info("gateway service register to cluster node done. start tcp server.");
    }

    private boolean exiting = false;

    public void exit(String reason) {
        log.info("gateway service exit...");
        exiting = true;
        server_.closeListener();
        server_.closeReceive();
        server_.close();

        server.shutdown();

        Threads.dispose();
        ManagerService.fini();
        NodeManager.getInstance().fini();
        RequestIDManager.getInstance().dispose();

        log.info("gateway service exit done[{}]", reason);
    }
}
