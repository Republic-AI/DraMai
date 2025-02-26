package com.infinity.ai.gateway.websocket.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private SslContext sslCtx;
    //单位是秒
    /*private final int READER_IDLE_TIME = 30;
    private final int WRITER_IDLE_TIME = 30;
    private final int ALL_IDLE_TIME = 30;*/

    public WebSocketChannelInitializer(SslContext sslContext) {
        this.sslCtx = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (this.sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(1048576));

        pipeline.addLast("iPAddressHandler", new IPAddressHandler());
        //用于处理websocket, /ws为访问websocket时的uri 增加最大帧长度至10 MB
        WebSocketServerProtocolConfig config = WebSocketServerProtocolConfig.newBuilder()
                .websocketPath("/")
                .maxFramePayloadLength(1048576)  // 设置为1 MB
                .build();
        pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(config));
        //pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, ALL_IDLE_TIME, TimeUnit.SECONDS));
        pipeline.addLast("myWebSocketHandler", new WebSocketHandler());
    }

}
