package com.infinity.manager.node;

public final class NodeConstant {
    // 节点前缀
    // 未知节点
    public final static char kUnknownNode = 'N';
    // 平台节点
    public final static char kPlatformService = 'P';
    // gateway节点
    public final static char kGatewayService = 'G';
    // 聊天gpt4服务节点
    public final static char kChatService = 'C';
    //定时服务
    public final static char kQuartzService = 'Q';
    //python Ai服务
    public final static char kPythonService = 'A';

    public final static String NODE_ID_SEP = ".";//游戏ID.N区.N服.节点ID(app.z1.s1.P1) serverId=游戏ID.N区.N服
}