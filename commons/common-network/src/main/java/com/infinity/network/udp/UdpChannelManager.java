package com.infinity.network.udp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UdpChannelManager {
    private static class UdpChannelManagerHolder {
        private static final UdpChannelManager kInstance = new UdpChannelManager();
    }

    private final Map<KcpOnUdp, UdpChannel> channels_;

    public static UdpChannelManager getInstance() {
        return UdpChannelManagerHolder.kInstance;
    }

    private UdpChannelManager() {
        channels_ = new ConcurrentHashMap<>();
    }

    public void addChannel(KcpOnUdp kcpOnUdp, UdpChannel udpChannel) {
        channels_.put(kcpOnUdp, udpChannel);
    }

    public UdpChannel getChannel(KcpOnUdp kcpOnUdp) {
        return channels_.get(kcpOnUdp);
    }

    public boolean contains(KcpOnUdp kcpOnUdp) {
        return channels_.containsKey(kcpOnUdp);
    }

    public UdpChannel removeChannel(KcpOnUdp kcpOnUdp) {
        return channels_.remove(kcpOnUdp);
    }
}
