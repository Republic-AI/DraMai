package com.infinity.common.msg.timer;

import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.GsonUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitExpridedMessage<T> extends TimerMessage<T> implements Serializable {

    @Override
    public int getCommand() {
        return ProtocolCommon.MSG_CODE_TIMER_SUBMIT;
    }

    public static int getCmd() {
        return ProtocolCommon.MSG_CODE_TIMER_SUBMIT;
    }

    //子类型
    private Integer smd;

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
}
