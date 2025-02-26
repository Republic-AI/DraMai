package com.infinity.ai.gateway.websocket;

import com.infinity.common.ConvertUtil;
import com.infinity.common.utils.LoadPropertiesFileUtil;
import com.infinity.protocol.ServerNode;
import com.infinity.register.NodeConfig;

import java.util.Properties;

public class GatewayConfig {
    private String nodeId;
    private String type;
    private String ip;
    private int port;
    private int maxChannel;
    private String taskPoolLength;
    private int serverport;
    private boolean sslEnable = false;
    private String certChainFile;
    private String keyFile;

    private NodeConfig nodeConfig;
    private ServerNode.server_node node_info_;

    private static class ConfigHolder {
        private static final GatewayConfig kInstance = new GatewayConfig();
    }

    public static GatewayConfig getInstance() {
        return ConfigHolder.kInstance;
    }

    private GatewayConfig() {
        init();
    }

    public void init(){
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

    private void readServiceConfig() {
        Properties properties = LoadPropertiesFileUtil.loadProperties("config/config.properties");
        nodeId = properties.getProperty("service.gateway.id");
        type = properties.getProperty("service.gateway.type");
        ip = properties.getProperty("service.gateway.ip");
        port = Integer.parseInt(properties.getProperty("service.gateway.port"));
        maxChannel = Integer.parseInt(properties.getProperty("service.gateway.maxChannel"));
        taskPoolLength = properties.getProperty("service.gateway.pool.length", "0");
        serverport = Integer.parseInt(properties.getProperty("service.gateway.net.port"));
        sslEnable = Boolean.valueOf(properties.getProperty("service.ssl.enable","false"));
        certChainFile = properties.getProperty("service.ssl.certChainFile");
        keyFile = properties.getProperty("service.ssl.keyFile");
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

    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public ServerNode.server_node getNode_info_() {
        return node_info_;
    }

    public int getTaskPoolLength() {
        return "0".equalsIgnoreCase(taskPoolLength) ? Runtime.getRuntime().availableProcessors() : Integer.parseInt(taskPoolLength);
    }

    public int getServerport() {
        return serverport;
    }

    public boolean isSslEnable() {
        return sslEnable;
    }

    public String getCertChainFile() {
        return certChainFile;
    }

    public String getKeyFile() {
        return keyFile;
    }
}
