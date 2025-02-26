package com.infinity.common.msg.timer;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

public abstract class TimerMessage<T> extends BaseMsg<T> {
    //服务器ID
    private String nodeId;
    //延迟时间，单位毫秒
    private long delay;
    //请求开始时间,发送请求前设置该值：System.currentTimeMillis()
    private long start;

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_TIMER;
    }

}
