package com.infinity.network;

public interface IConnectorEvent {
    void OnConnect(final IConnector connector);

    void OnDisConnect(final IConnector connector);

    void OnError(final IConnector connector, final String errorMessage);
}