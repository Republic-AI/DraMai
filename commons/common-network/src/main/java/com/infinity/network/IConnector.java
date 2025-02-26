package com.infinity.network;

import com.infinity.manager.node.IChecker;

import java.nio.ByteBuffer;

public interface IConnector {
    int getPeerPort();

    String getPeerAddress();

    int getChannelID();

    String getNodeID();

    boolean isLinked();

    void connect(final String peerAddress, final int peerPort, final String nodeID);

    /*void read();
    void write(final byte[] writeData, final int dataLength);*/
    boolean isClosed();

    boolean availability();

    void write(final ByteBuffer writeBuffer);

    void register(final IConnectorEvent connectorEvent);

    void unregister(final IConnectorEvent connectorEvent);

    IChecker getChecker();

    void dispose(String msg);
}