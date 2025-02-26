package com.infinity.ai.gateway.websocket.net;

import com.infinity.ai.gateway.websocket.GatewayConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;

@Slf4j
public class WebSocketServer {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start() throws InterruptedException {
        GatewayConfig config = GatewayConfig.getInstance();
        boolean ssl = config.isSslEnable();
        SslContext sslCtx = null;
        try {
            if (ssl) {
                File certChainFile = new File("config/" + config.getCertChainFile());
                File keyFile = new File("config/" + config.getKeyFile());
                sslCtx = SslContextBuilder.forServer(certChainFile, keyFile)
                        .clientAuth(ClientAuth.NONE)
                        //.trustManager(caCertFile)
                        .sslProvider(SslProvider.JDK)
                        .build();
            }
        } catch (SSLException e) {
            e.printStackTrace();
        }

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_RCVBUF, 1048576)  // 设置TCP接收缓冲区大小为1 MB
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new WebSocketChannelInitializer(sslCtx));
            ChannelFuture channelFuture = serverBootstrap.bind(GatewayConfig.getInstance().getServerport()).sync();
            log.info("websocket 网关启动成功，端口:" + GatewayConfig.getInstance().getServerport());
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
