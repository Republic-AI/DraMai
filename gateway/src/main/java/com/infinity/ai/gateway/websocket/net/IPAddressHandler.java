package com.infinity.ai.gateway.websocket.net;

import com.infinity.common.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class IPAddressHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final AttributeKey<String> CLIENT_IP = AttributeKey.valueOf("CLIENT_IP");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        HttpHeaders headers = request.headers();

        // 从 X-Forwarded-For 或 X-Real-IP 获取真实IP
        String clientIP = headers.get("X-Forwarded-For");
        if (clientIP == null) {
            clientIP = headers.get("X-Real-IP");
        }

        // 如果没有通过代理，直接从 remoteAddress 获取
        if (StringUtils.isEmpty(clientIP)) {
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIP = socketAddress.getAddress().getHostAddress();
        }

        // 将IP地址保存到Channel的属性中
        ctx.channel().attr(CLIENT_IP).set(clientIP);

        // 将请求传递给下一个处理器
        ctx.fireChannelRead(request.retain());
    }
}
