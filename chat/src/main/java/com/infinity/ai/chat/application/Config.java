package com.infinity.ai.chat.application;

import com.infinity.common.ConvertUtil;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.utils.LoadPropertiesFileUtil;
import com.infinity.protocol.ServerNode;
import com.infinity.register.NodeConfig;
import lombok.Getter;

import java.util.*;

public class Config {
    private String nodeId;
    private String type;
    private String ip;
    private int port;
    private int maxChannel;

    @Getter
    private NodeConfig nodeConfig;
    @Getter
    private ServerNode.server_node node_info_;

    @Getter
    private String gameDataPath;
    private String taskPoolLength;

    private Map<String, Integer> apiKeys = new HashMap<>();
    private List<String> tokens = new ArrayList<>();

    private static class ConfigHolder {
        private static final Config kInstance = new Config();
    }

    public static Config getInstance() {
        return ConfigHolder.kInstance;
    }

    private Config() {
        init();
    }

    public void init() {
        readServiceConfig();

        nodeConfig = new NodeConfig();
        nodeConfig.setNodeId(this.nodeId);
        nodeConfig.setType(this.type);
        nodeConfig.setIp(this.ip);
        nodeConfig.setPort(this.port);
        nodeConfig.setMaxChannel(this.maxChannel);

        ServerNode.server_node.Builder builder = ServerNode.server_node.newBuilder();
        builder.setIpLan(ConvertUtil.ipv4Str2Int(this.ip));
        builder.setPortLan(this.port);
        builder.setNodeId(this.nodeId);
        builder.setChannel(1);
        builder.setType(nodeConfig.getType());
        node_info_ = builder.build();
    }


    public void reload() {
        init();
    }

    private void readServiceConfig() {
        Properties properties = LoadPropertiesFileUtil.loadProperties("config/config.properties");
        nodeId = properties.getProperty("service.chat.id");
        type = properties.getProperty("service.chat.type");
        ip = properties.getProperty("service.chat.ip");
        port = Integer.parseInt(properties.getProperty("service.chat.port"));
        maxChannel = Integer.parseInt(properties.getProperty("service.chat.maxChannel"));
        gameDataPath = properties.getProperty("game.data.path");
        taskPoolLength = properties.getProperty("game.task.pool.length", "0");
        GameConfigManager.getInstance().loadConfig(gameDataPath);
    }

    public int getTaskPoolLength() {
        return "0".equalsIgnoreCase(taskPoolLength) ? Runtime.getRuntime().availableProcessors() : Integer.parseInt(taskPoolLength);
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getMaxChannel() {
        return maxChannel;
    }

}