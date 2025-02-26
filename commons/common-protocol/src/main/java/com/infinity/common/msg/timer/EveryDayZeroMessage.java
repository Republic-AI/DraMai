package com.infinity.common.msg.timer;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.GsonUtil;
import lombok.Data;
import lombok.Getter;

@Data
public class EveryDayZeroMessage extends BaseMsg {

    @Override
    public int getCommand() {
        return ProtocolCommon.MSG_CODE_TIMER_EVERYDAYZERO;
    }

    public static int getCmd() {
        return ProtocolCommon.MSG_CODE_TIMER_EVERYDAYZERO;
    }

    //NotifyType类型：0：每天0点，1：每周一0点，2：每个月1号
    public int notifyType;

    public String src;

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }

    @Getter
    public enum NotifyType {
        DAY(0),
        WEEK(1),
        MONTH(2);

        private final int code;

        NotifyType(int code) {
            this.code = code;
        }

        public static boolean isDay(int code) {
            return NotifyType.DAY.getCode() == code;
        }

        public static boolean isWeek(int code) {
            return NotifyType.WEEK.getCode() == code;
        }

        public static boolean isMonth(int code) {
            return NotifyType.MONTH.getCode() == code;
        }
    }
}
