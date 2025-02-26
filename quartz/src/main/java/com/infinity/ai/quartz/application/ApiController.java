package com.infinity.ai.quartz.application;

import com.infinity.ai.quartz.config.QuartzConfig;
import com.infinity.common.msg.common.RefreshMsg;
import com.infinity.common.msg.timer.EveryDayZeroMessage;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeManager;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {

    /**
     * 刷新配置
     *
     * @param server all:所有服务器，P:所有platform，C：所有chatgpt, Q:quartz, G:gateway
     * @return
     */
    @GetMapping("/config/refresh/{server}")
    private String refreshConfig(@PathVariable("server") String server) {
        log.debug("start refresh game config ......,server={}", server);
        if (StringUtils.isEmpty(server)) {
            return "refresh fail, server can't null";
        }

        //server all:所有服务器，P:所有platform，C：所有chatgpt, Q:quartz, G:gateway
        if (QuartzConfig.getInstance().getNodeId().equals(server) || "DD".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            QuartzConfig.getInstance().init();
        } else if ("ALL".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            QuartzConfig.getInstance().init();
            //通知其他服务器
            MessageSender.getInstance().broadcastMessageToAllService(RefreshMsg.builder().build());
        } else if (server.trim().length() == 1) {
            log.info("refresh {} config", server);
            MessageSender.getInstance().sendMessage(server.toUpperCase().charAt(0), RefreshMsg.builder().build());
        } else {
            //指定服务器ID刷新
            Map<String, INode> allServices = NodeManager.getInstance().getAllNodes();
            INode iNode = allServices.get(server);
            if (iNode != null) {
                log.info("refresh {} config", server);
                MessageSender.getInstance().sendMessage(server, RefreshMsg.builder().build());
            }
        }

        log.debug("refresh game config success......");
        return "操作成功";
    }

    //刷新配置
    @GetMapping("/test")
    private String test() {
        EveryDayZeroMessage msg = new EveryDayZeroMessage();

        /*Collection<ServiceStatus> findServiceStatus = ServiceStatusHelper.findServiceStatus(SystemConsts.SERVICE_TYPE_PLATFORM);
        for (ServiceStatus serviceStatus : findServiceStatus) {
            VertxMessageHelper.sendMessageToService(serviceStatus.getServiceQueueName(), msg.toString());
        }
*/
        return "操作成功";
    }
}
