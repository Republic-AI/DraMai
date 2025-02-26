package com.infinity.network;

public final class Constant {
    public final static int kProtocolLength = 4;
    public final static int kBufferSize = 1024;

    // 最大包体不得超过1MB
    public final static int kMaxPacketLength = 1048576; // 1MB


    // 无数据收到时认为连接无效
    public final static long kChannelAliveMS = 60 * 1000;
}