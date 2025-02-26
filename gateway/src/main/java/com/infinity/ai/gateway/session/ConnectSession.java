package com.infinity.ai.gateway.session;

import io.netty.channel.Channel;

public class ConnectSession {
    private String sesseionId;
    private long userId;
    private Channel channel;

    public ConnectSession(String sessionId, long userId) {
        this.sesseionId = sessionId;
        this.userId = userId;
    }

    public Channel getChannel() {
        return channel;
    }

    public ConnectSession setChannel(Channel ch) {
        this.channel = ch;
        return this;
    }

    public void setSesseionId(String sesseionId) {
        this.sesseionId = sesseionId;
    }

    public String getSesseionId() {
        return sesseionId;
    }

    public long getUserId() {
        return userId;
    }
}
