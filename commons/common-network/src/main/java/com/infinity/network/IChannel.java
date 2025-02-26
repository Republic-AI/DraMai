package com.infinity.network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public interface IChannel extends java.lang.Comparable {
    int getID();

    void setNodeID(final String nodeID);

    String getNodeID();

    boolean isClosed();

    boolean availability();

    void write(ByteBuffer byteBuffer);

    void write(byte[] data, int length);

    void close();

    void closeWrite();

    void closeReceive();


    void registerEventHandle(IChannelEvent channelEvent);

    void unregisterEventHandle(IChannelEvent channelEvent);

    default public void popWriteDone() {
    }

    default public AsynchronousSocketChannel getSocketHandler() {
        return null;
    }

    default public void updateReceiveTime() {
    }

    ;
}