package com.infinity.manager.node;

import com.infinity.network.IChannel;
import com.infinity.network.IChannelEvent;
import com.infinity.protocol.ServerNode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceNode implements INode, IChannelEvent {
    private final Lock channel_lock_;
    private final List<IChannel> socket_channels_;
    private final ServerNode.server_node node_info_;

    public ServiceNode(final ServerNode.server_node nodeInfo) {
        channel_lock_ = new ReentrantLock();
        socket_channels_ = new ArrayList<>();
        node_info_ = nodeInfo;
    }

    @Override
    public char getType() {
        return node_info_.getType() == null ? NodeConstant.kUnknownNode : node_info_.getType().charAt(0);
    }

    @Override
    public String getNodeID() {
        return node_info_.getNodeId();
    }

    @Override
    public <T> T getNodeMeta() {
        return (T) node_info_;
    }

    @Override
    public void addChannel(IChannel newChannel) {
        try {
            channel_lock_.lock();
            if (!socket_channels_.contains(newChannel)) {
                newChannel.setNodeID(this.getNodeID());
                socket_channels_.add(newChannel);
                newChannel.registerEventHandle(this);
            }
        } finally {
            channel_lock_.unlock();
        }
    }

    @Override
    public void removeChannel(IChannel aChannel) {
        try {
            channel_lock_.lock();
            if (socket_channels_.contains(aChannel)) {
                socket_channels_.remove(aChannel);
                aChannel.unregisterEventHandle(this);
            }
        } finally {
            channel_lock_.unlock();
        }
    }

    @Override
    public IChannel getChannel() {
        long currentTime = System.currentTimeMillis();
        final int channelSize = socket_channels_.size();
        assert channelSize > 0;
        if (channelSize <= 0) {
            return null;
        }

        long index = currentTime % channelSize;
        IChannel channel = socket_channels_.get((int) index);
        if (channel.availability()) {
            return channel;
        }
        try {
            channel_lock_.lock();
            Iterator<IChannel> socketChannel = socket_channels_.iterator();
            while (socketChannel.hasNext()) {
                // WARNING: 可能是没有
                channel = socketChannel.next();
                if (channel.availability())
                    return channel;
            }
        } finally {
            channel_lock_.unlock();
        }

        assert (channel != null) : "failed to get node channel.";
        return channel;
    }

    // 注意，这里对节点里的所有链接通道都广播
    public void write(ByteBuffer byteBuffer) {
        int length = byteBuffer.remaining();
        byte[] data = new byte[length];
        byteBuffer.get(data);
        write(data, data.length);
    }

    @Override
    public void write(byte[] data, int length) {
        try {
            channel_lock_.lock();
            Iterator<IChannel> socketChannel = socket_channels_.iterator();
            if (socketChannel != null && socketChannel.hasNext()) {
                IChannel channel = socketChannel.next();
                if (channel != null)
                    channel.write(data, length);
            }
        } finally {
            channel_lock_.unlock();
        }
    }

    @Override
    public void dispose() {
        try {
            channel_lock_.lock();
            if (socket_channels_ != null)
                socket_channels_.clear();
        } finally {
            channel_lock_.unlock();
        }
    }

    @Override
    public void onDispose(IChannel socketChannel) {
        if (socketChannel == null)
            return;

        try {
            channel_lock_.lock();
            if (socket_channels_ != null && socket_channels_.contains(socketChannel)) {
                socketChannel.unregisterEventHandle(this);
                socket_channels_.remove(socketChannel);

                if (socket_channels_.size() == 0) {
                    NodeManager.getInstance().unregister(this.getNodeID());
                }
            }
        } finally {
            channel_lock_.unlock();
        }
    }
}