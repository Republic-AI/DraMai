package com.infinity.ai.quartz.application;

import com.infinity.ai.quartz.QuartzService;
import com.infinity.ai.quartz.config.QuartzConfig;
import com.infinity.ai.quartz.manager.CronTaskManager;
import com.infinity.common.base.game.IGameStarter;
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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
public final class QuartzStarter implements IGameStarter {
    private ApplicationContext context = null;
    private static QuartzStarter kInstance = null;
    private TcpServer server_ = null;

    private QuartzStarter() {

    }

    public static QuartzStarter getInstance() {
        if (kInstance == null)
            kInstance = new QuartzStarter();
        return kInstance;
    }

    public final void setAppContext(ApplicationContext appContext) {
        if (context == null)
            context = appContext;
    }

    @Override
    public final void start() {
        log.info(QuartzService.class.toString());
        log.info("=========================================================");
        log.info("start Quartz service. version={}", Version.version);
        QuartzConfig config = QuartzConfig.getInstance();
        Threads.regist(new MultiParam<NameParam>()
                .append(new NameParam(ThreadConst.TIMER_1S, 1, 1000))
                //.append(new NameParam(ThreadConst.QUEUE_LOGIC, 4))
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
        MessageSender.getInstance().setMyNode(config.getNodeConfig());
    }

    @Override
    public final void run() {
        QuartzConfig config = QuartzConfig.getInstance();

        String tcpAddress = config.getIp();
        int tcpPort = config.getPort();

        log.info("quartz service listen on {}:{}", tcpAddress, tcpPort);

        final var listenerAddress = "0.0.0.0";
        server_ = new TcpServer(listenerAddress, tcpPort);
        server_.start();

        log.info("start quartz service done. version={}", Version.version);
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
        log.info("quartz service register to cluster node done. start tcp server.");

        //集群连接后启动任务
        log.info("Cron task init......");
        CronTaskManager.getInstance().init();

        addShutdownHook();
        log.info("Quartz service success.");
    }

    private boolean exiting = false;

    @Override
    public void exit(String reason) {
        SpringApplication.exit(context, () -> {
            release(reason);
            return 0;
        });
    }

    private void release(String reason){
        log.info("Quartz service exit...");
        Threads.dispose();
        ManagerService.fini();
        RequestIDManager.getInstance().dispose();
        CronTaskManager.getInstance().shutdownJobs();
        log.info("Quartz service exit done[{}]", reason);
    }

    private void addShutdownHook(){
        // 注册一个关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> release("")));
    }
}
