package com.infinity.network;

import com.infinity.manager.node.CenterNode;
import com.infinity.manager.node.NodeManager;
import com.infinity.network.codec.CodecFactory;
import com.infinity.network.codec.ICodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.WritePendingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Channel implements IChannel {
    private final AsynchronousSocketChannel socket_;
    private final int channel_id_;
    private boolean closed_ = false;
    private ReadHandler read_handler_;
    private WriteHandler write_handler_;
    private WriteCache write_cache_;
    private final ICodec protocol_codec_;

    private final Lock event_lock_;
    private final Set<IChannelEvent> channel_event_;

    private String node_id_;
    // 最后接收数据的时间
    private long last_receive_time_;

    public Channel(int channelId, final AsynchronousSocketChannel newSocket, final ChannelOption channelOption) {
        event_lock_ = new ReentrantLock();
        channel_event_ = new HashSet<>();

        write_cache_ = new WriteCache();
        socket_ = newSocket;
        channel_id_ = channelId;//ManagerService.getChannelIDManager().newID();
        protocol_codec_ = CodecFactory.Create();

        // tcp各项参数
        if (channelOption != null) {
            try {
                newSocket.setOption(StandardSocketOptions.TCP_NODELAY, channelOption.isNoDelay());
                newSocket.setOption(StandardSocketOptions.SO_SNDBUF, channelOption.getSendBufferSize());
                newSocket.setOption(StandardSocketOptions.SO_RCVBUF, channelOption.getReceiveBufferSize());
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        ManagerService.getChannelManager().add(this);
        last_receive_time_ = System.currentTimeMillis();
    }


    public Channel(final AsynchronousSocketChannel newSocket, final ChannelOption channelOption) {
        this(ManagerService.getChannelIDManager().newID(), newSocket, channelOption);
    }

    @Override
    public int getID() {
        return channel_id_;
    }

    @Override
    public void setNodeID(final String nodeID) {
        node_id_ = nodeID;
    }

    @Override
    public String getNodeID() {
        return node_id_;
    }

    @Override
    public void registerEventHandle(IChannelEvent channelEvent) {
        if (channel_event_.contains(channelEvent))
            return;

        event_lock_.lock();
        channel_event_.add(channelEvent);
        event_lock_.unlock();
    }

    @Override
    public void unregisterEventHandle(IChannelEvent channelEvent) {
        if (!channel_event_.contains(channelEvent))
            return;

        event_lock_.lock();
        channel_event_.remove(channelEvent);
        event_lock_.unlock();
    }

    @Override
    public boolean isClosed() {
        return closed_ || (socket_ != null && !socket_.isOpen());
    }

    @Override
    public boolean availability() {
        return !isClosed();
    }

    public AsynchronousSocketChannel getSocketHandler() {
        return socket_;
    }

    public void read() {
        if (!socket_.isOpen()) {
            log.error("socket is closed.", channel_id_);
            return;
        }
        try {
            log.debug("channel is accept and start receive. rAddr: {}, linkID={}",
                    socket_.getRemoteAddress().toString(), channel_id_);
            if (read_handler_ == null)
                read_handler_ = new ReadHandler(protocol_codec_);
            read_handler_.read(this);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuffer byteBuffer) {
        int length = byteBuffer.remaining();
        byte[] data = new byte[length];
        byteBuffer.get(data);
//        LoggerHelper.debug("channel write(ByteBuffer byteBuffer)->writData. length=%d, linkID=%d, nodeID=%s, cacheSize=%d",
//            length, channel_id_, node_id_, write_cache_.size());
        write(data, data.length);
    }

    @Override
    public void write(byte[] data, int length) {
        if (write_handler_ == null)
            write_handler_ = new WriteHandler();

        int totalLength = length + 4;
        ByteBuffer newRawData = ByteBuffer.allocate(totalLength);
        newRawData.putInt(length);
        newRawData.put(data, 0, length);
        newRawData.flip();

        synchronized (write_cache_) {
            boolean sendNow = write_cache_.isEmpty();
//            LoggerHelper.debug("channel write(byte[] data, int length)->writData. length=%d, linkID=%d, nodeID=%s, sendNow=%b, cacheSize=%d",
//                totalLength, channel_id_, node_id_, sendNow, write_cache_.size());
            write_cache_.push(newRawData);
            if (sendNow)
                writeNext();
        }
    }

    public void popWriteDone() {
        try {
            synchronized (write_cache_) {
//                LoggerHelper.debug("channel popWriteDone. length=%d, linkID=%d, nodeID=%s",
//                    write_cache_.size(), channel_id_, node_id_);

                if (write_cache_.isEmpty())
                    return;

                // 如果上次没有写完，继续写
                ByteBuffer buffer = write_cache_.peek();
                if (buffer.hasRemaining()) {
//                    LoggerHelper.debug("channel continue send buffer. remainingLength=%d, linkID=%d",
//                        buffer.remaining(), channel_id_);
                    socket_.write(buffer, this, write_handler_);
                } else {
                    write_cache_.pop();
//                    LoggerHelper.debug("channel pop writeData. length=%d, linkID=%d, nodeID=%s",
//                        write_cache_.size(), channel_id_, node_id_);
                    writeNext();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("channel popWriteDone error. msg={}, linkID:{}", e.getMessage(), channel_id_);
            close();
        }
    }

    public void writeNext() {
        synchronized (write_cache_) {
            if (write_cache_.isEmpty())
                return;

//            LoggerHelper.debug("channel writeNext. length=%d, linkID=%d, nodeID=%s",
//                write_cache_.size(), channel_id_, node_id_);

            ByteBuffer buffer = write_cache_.peek();

            try {
                if (buffer != null && buffer.hasRemaining())
                    socket_.write(buffer, this, write_handler_);
                else {
                    log.error("channel writeNext error {}, remote: {}, buffer==null->{}, remaining: {},",
                            channel_id_, socket_.getRemoteAddress().toString(), buffer == null, buffer != null ? buffer.remaining() : 0);
                    if (buffer != null && buffer.remaining() <= 0)
                        write_cache_.pop();
                }
            } catch (IOException | WritePendingException | NotYetConnectedException | ShutdownChannelGroupException e) {
                log.error("channel writeNext error {}, linkID={}, nodeID={}", e.getMessage(), channel_id_, node_id_);
                e.printStackTrace();
                close();
            }
        }
    }

    @Override
    public void close() {
        System.out.println("==========连接关闭了");
        //todo 移除CenterNode 中connectors_中对于connector，否则不会重连
        if (isClosed()) return;
        log.debug("close the channel. nodeID={}, linkID={}", node_id_, channel_id_);
        closed_ = true;
        // 关闭连接
        SocketCommon.close(socket_);

        // 通知socket通道已经释放事件
        event_lock_.lock();
        for (IChannelEvent channelEvent : channel_event_)
            channelEvent.onDispose(this);

        //清除CenterNode中的Connector
        CenterNode centerNode = NodeManager.getInstance().getPositiveNode(node_id_);
        centerNode.clearConnector(channel_id_);

        channel_event_.clear();
        event_lock_.unlock();

        ManagerService.getChannelManager().remove(this);
    }

    @Override
    public void closeWrite() {
        try {
            socket_.shutdownOutput();
        } catch (IOException e) {
            log.error("failed shutdownOutput. msg={}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void closeReceive() {
        try {
            socket_.shutdownInput();
        } catch (IOException e) {
            log.error("failed shutdownInput. msg={}", e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        try {
            if (socket_.isOpen())
                return String.format("C%d:%s", channel_id_, socket_.getRemoteAddress().toString());
            else
                return String.format("C%d", channel_id_);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("C%d", channel_id_);
    }

    @Override
    public int compareTo(Object o) {
        Channel cmpChannel = (Channel) o;
        if (cmpChannel == null)
            return -1;
        return cmpChannel.getID() - this.getID();
    }

    public boolean checkAlive() {
        return true;
        /*final long nowTime = System.currentTimeMillis();
        final long expireTime = last_receive_time_ + Constant.kChannelAliveMS;
        return nowTime <= expireTime;*/
    }

    public void updateReceiveTime() {
        last_receive_time_ = System.currentTimeMillis();
    }
}