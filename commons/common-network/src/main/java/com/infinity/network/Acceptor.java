package com.infinity.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class Acceptor {
    private AsynchronousServerSocketChannel accept_socket_;
    private ChannelOption channel_option_;
    private final String bind_address_;
    private final int bind_port_;
    private final InetSocketAddress address_;
    private AsynchronousChannelGroup thread_group_ = null;
    private boolean exit_ = false;

    public Acceptor(String bindAddress, int bindPort) {
        bind_address_ = bindAddress;
        bind_port_ = bindPort;
        address_ = new InetSocketAddress(bind_address_, bind_port_);
        channel_option_ = new ChannelOption();
        log.info("bind address.ip={}:{}", bind_address_, bind_port_);
    }

    public boolean isOpen() {
        return !exit_ && accept_socket_.isOpen();
    }

    public void listen() throws Exception {
        // 默认线程池数量是cpu的核数+1
        int totalCpu = Runtime.getRuntime().availableProcessors();
        listen(totalCpu + 1);
    }

    public void listen(int threadNum) throws Exception {
        thread_group_ = AsynchronousChannelGroup.withFixedThreadPool(threadNum, new NetThreadPool("socket_on_" + bind_port_));
        accept_socket_ = AsynchronousServerSocketChannel.open(thread_group_);
        accept_socket_.setOption(StandardSocketOptions.SO_REUSEADDR, false);

        if (accept_socket_.bind(address_) == null) {
            log.error("AsyncSocket bind error addr {}", address_);
            return;
        }
        accept_socket_.accept(this, new AcceptHandler(channel_option_));
        log.info("acceptor start listen.");
    }

    public void close() {
        exit_ = true;
        if (accept_socket_ == null)
            return;

        try {
            accept_socket_.close();
        } catch (IOException e) {
            log.error("close acceptor socket error. msg={}", e.getMessage());
            e.printStackTrace();
        }

        try {
            if (!thread_group_.isShutdown()) {
                thread_group_.shutdown();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void accept(Acceptor acceptor,
                       CompletionHandler<AsynchronousSocketChannel, Acceptor> handler) {
        if (exit_)
            return;

        if (accept_socket_ == null) {
            log.error("the accept socket is null.");
            return;
        }

        accept_socket_.accept(acceptor, handler);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", bind_address_, bind_port_);
    }
}
