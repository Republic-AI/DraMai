package com.infinity.network.udp;

import com.infinity.network.IChannel;
import com.infinity.network.IChannelEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.ByteBuffer;

public class UdpChannel implements IChannel {
    private String node_id_;
    protected final KcpOnUdp kcp_on_udp_;
    private final int channel_id_;

    public UdpChannel(KcpOnUdp kcpOnUdp, int chanId) {
        kcp_on_udp_ = kcpOnUdp;
        channel_id_ = chanId;
        kcp_on_udp_.setSessionId(String.format("S%d", channel_id_));
    }

    public KcpOnUdp getKcpOnUdp() {
        return this.kcp_on_udp_;
    }

    @Override
    public int getID() {
        return channel_id_;
    }

    @Override
    public void setNodeID(String nodeID) {
        node_id_ = nodeID;
    }

    @Override
    public String getNodeID() {
        return node_id_;
    }

    @Override
    public boolean isClosed() {
        return kcp_on_udp_ == null || kcp_on_udp_.isClosed();
    }

    @Override
    public boolean availability() {
        return kcp_on_udp_ != null && !kcp_on_udp_.isClosed();
    }

    @Override
    public void write(ByteBuffer byteBuffer) {
        ByteBuf content = PooledByteBufAllocator.DEFAULT.buffer(byteBuffer.remaining());
        content.writeBytes(byteBuffer);
        kcp_on_udp_.send(content);
    }

    @Override
    public void write(byte[] data, int length) {
        ByteBuf content = PooledByteBufAllocator.DEFAULT.buffer(length);
        content.writeBytes(data);
        kcp_on_udp_.send(content);
    }

    @Override
    public void close() {
        if (kcp_on_udp_ != null && !kcp_on_udp_.isClosed()) {
            kcp_on_udp_.close();
        }
    }

    @Override
    public void closeWrite() {
    }

    @Override
    public void closeReceive() {
    }


    @Override
    public void registerEventHandle(IChannelEvent channelEvent) {
    }

    @Override
    public void unregisterEventHandle(IChannelEvent channelEvent) {
    }

    @Override
    public int compareTo(Object o) {
        UdpChannel cmpChannel = (UdpChannel) o;
        if (cmpChannel == null)
            return -1;


        if (cmpChannel.kcp_on_udp_.equals(this.kcp_on_udp_))
            return 0;

        return cmpChannel.getID() - this.getID();
    }
}