package com.infinity.network;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpServer {
    private final Acceptor acceptor_;

    public TcpServer(String hostAddress, int hostPort) {
        acceptor_ = new Acceptor(hostAddress, hostPort);
    }

    public final void start() {
        try {
            acceptor_.listen();
        } catch (Exception exception) {
            log.error("listen tcp server error. msg={}", exception.getMessage());
            exception.printStackTrace();
            acceptor_.close();
            System.exit(-1);
        }
    }

    public final void closeListener() {
        acceptor_.close();
    }

    public final void closeReceive() {
        ManagerService.getChannelManager().closeReceive();
    }

    public final void close() {
        if (acceptor_.isOpen())
            closeListener();

        ManagerService.getChannelManager().dispose();
    }
}