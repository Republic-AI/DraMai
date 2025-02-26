package com.infinity.ai.platform.npc.goap.action.data;

import com.infinity.common.utils.GsonUtil;

public abstract class BaseData {

    public String toJsonString() {
        return GsonUtil.parseObject(this);
    }
}
