package com.infinity.ai.gateway.session;

import io.netty.channel.Channel;

public class ChannelAttach {
    private Channel channel;
    private long activityTime;
    // private ContentCodec codec;

    public ChannelAttach(Channel channel, long activityTime) {
        this.channel = channel;
        this.activityTime = activityTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(long activityTime) {
        this.activityTime = activityTime;
    }

    /*
     * public ContentCodec getCodec() { return codec; }
     *
     * public void setCodec(ContentCodec codec) { this.codec = codec; }
     */
}
