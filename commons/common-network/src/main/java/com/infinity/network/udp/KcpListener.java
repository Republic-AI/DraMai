package com.infinity.network.udp;

import io.netty.buffer.ByteBuf;

public interface KcpListener {

    /**
     * kcp message
     *
     * @param bb the data
     */
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp);

    /**
     * kcp异常，之后此kcp就会被关闭
     *
     * @param ex  异常
     * @param kcp 发生异常的kcp，null表示非kcp错误
     */
    public void handleException(Throwable ex, KcpOnUdp kcp);

    /**
     * 关闭
     */
    public void handleClose(KcpOnUdp kcp);
}
