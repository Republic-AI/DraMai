package com.infinity.manager.node;

import com.infinity.network.IChannel;

import java.nio.ByteBuffer;

public interface INode {
    char getType();

    String getNodeID();

    void addChannel(IChannel newChannel);

    void removeChannel(IChannel aChannel);

    IChannel getChannel();

    void write(ByteBuffer byteBuffer);

    void write(byte[] data, int length);

    <T> T getNodeMeta();

    void dispose();
}