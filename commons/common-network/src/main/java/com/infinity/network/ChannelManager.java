package com.infinity.network;

import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.Threads;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class ChannelManager {
    private final Map<Integer, Channel> channel_collection_ = new ConcurrentHashMap<>();
    private boolean check_live_exit_ = false;

    public ChannelManager() {

        Threads.regist(new NameParam(NetThreadConst.TIMER_chan_check_live, 1, 30000));
        Threads.addListener(NetThreadConst.TIMER_chan_check_live, "network#chan_check_live", this::onCheckLoop);
    }

    public final void add(Channel channel) {
        if (channel == null)
            return;

        channel_collection_.put(channel.getID(), channel);
    }

    public final void remove(Channel channel) {
        if (channel == null)
            return;
        channel_collection_.remove(channel.getID());
    }

    public final void closeReceive() {
        for (Channel channel : channel_collection_.values()) {
            if (channel == null || channel.isClosed())
                continue;
            channel.closeReceive();
        }
    }

    public final Channel getChannel(int channelID) {
        return channel_collection_.get(channelID);
    }

    public final void dispose() {
        check_live_exit_ = true;

        for (Channel channel : channel_collection_.values()) {
            if (channel == null || channel.isClosed())
                continue;
            channel.close();
        }
        channel_collection_.clear();
    }

    private boolean onCheckLoop(int interval) {
        if (check_live_exit_) return true;

        List<Integer> removeChannel = new LinkedList<Integer>();
        channel_collection_.forEach((channelID, channel) ->
        {
            if (channel == null || channel.isClosed())
                return;

            if (channel.checkAlive())
                return;

            removeChannel.add(channelID);
            log.error("node[{}]'s chan[{}] go to close by long idle", channel.getNodeID(), channelID);
            channel.close();
        });

        if (!removeChannel.isEmpty()) {
            for (Integer channelID : removeChannel) {
                channel_collection_.remove(channelID);
            }
        }
        return false;
    }
}