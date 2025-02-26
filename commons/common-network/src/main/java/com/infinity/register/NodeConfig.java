package com.infinity.register;


import java.io.Serializable;

public class NodeConfig implements Serializable {
    private static final long serialVersionUID = 7312914784691240088L;
    //节点ID
    private String nodeId;
    //节点类型：G：网关,P:Platform,C:chatgpt,Q:定时服务
    private String type;
    //节点IP地址
    private String ip;
    //节点端口
    private int port;
    //最大接连接数
    private int maxChannel;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxChannel() {
        return maxChannel;
    }

    public void setMaxChannel(int maxChannel) {
        this.maxChannel = maxChannel;
    }
}
