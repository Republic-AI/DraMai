package com.infinity.ai.login.controller;

import com.infinity.ai.login.application.Config;
import com.infinity.common.msg.common.RefreshMsg;
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
        if (Config.getInstance().getNodeId().equals(server) || "DD".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            Config.getInstance().init();
        } else if ("ALL".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            Config.getInstance().init();
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
}
