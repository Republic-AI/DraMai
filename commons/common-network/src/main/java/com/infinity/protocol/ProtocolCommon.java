package com.infinity.protocol;

// 协议描述:
// 协议的基本格式是数据长度+数据体
//
// 命令号定义:
// 注意节点间的通信命令一般定义为负数，
// 客户端和服务器间的命令定义为正数，以区别
//
//
//    长度 + 包头 + 包体
//    长度: 4字节
//    包头: 长度 + Header数据
//    包体: 长度 + packet数据
// gateway:      接入节点
// platform:     登陆节点

public final class ProtocolCommon {
    // 请求标志
    public final static String kRequestTag = "!";
    // 回应标志
    public final static String kResponseTag = "@";

    public final static String kErrorTag = "#";

    public final static int dstScope_Rand = 0;// 消息范围 随机1个
    public final static int dstScope_All = 1;// 消息范围 所有

    // 注册命令，服务器节点间的注册，需要注意，在管理节点中，服务器需要在数据中配置节点信息
    // 发送者: 服务节点
    // 接受者: 管理节点
    // 参数： server_node
    // 回应： OK
    public final static int kRegisterNodeCommand = -1;

    // 更新节点信息，只有gateway到管理节点需要
    // 发送者: gateway节点
    // 接收者: 管理节点
    // 参数：query_node_request
    // 回应：query_node_response
    public final static int kUpdateNodeCommand = -2;

    /**
     * 玩家断线
     * 命令ID： -9998
     */
    public static final int kG2PClientClose = -9998;

    /// <summary>
    /// 服务器的心跳命令,要非常简单
    /// </summary>
    public static final int kHeartBeatCommand = -9999;

    // 回应ok
    public final static int kResponseOK = 0;


}
