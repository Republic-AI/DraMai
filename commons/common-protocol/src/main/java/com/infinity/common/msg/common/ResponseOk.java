package com.infinity.common.msg.common;


import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.GsonUtil;
import lombok.Data;

//服务端响应消息
@Data
public class ResponseOk extends BaseMsg {

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getCommand() {
        return 0;
    }

    public static int getCmd() {
        return ProtocolCommon.MSG_ERROR;
    }

    public static ResponseOk build(BaseMsg msg) {
        ResponseOk responseOk = new ResponseOk();
        responseOk.setSessionId(msg.getSessionId());
        responseOk.setType(msg.getType());
        responseOk.setCommand(msg.getCommand());
        responseOk.setPlayerId(msg.getPlayerId());
        return responseOk;
    }

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
}
