package com.infinity.protocol;

import com.infinity.common.utils.GsonUtil;

public class HeartMessage {
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
}
