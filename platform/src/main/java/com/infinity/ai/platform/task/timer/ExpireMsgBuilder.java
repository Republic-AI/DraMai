package com.infinity.ai.platform.task.timer;

import com.infinity.ai.platform.application.Config;
import com.infinity.common.msg.timer.SubmitExpridedMessage;
import com.infinity.network.RequestIDManager;

public class ExpireMsgBuilder {

    public static SubmitExpridedMessage buildMsg(int cmd, long delay, long playerId) {
        SubmitExpridedMessage msg = new SubmitExpridedMessage();
        msg.setRequestId(RequestIDManager.getInstance().RequestID(false));
        msg.setPlayerId(playerId);
        msg.setNodeId(Config.getInstance().getNodeId());
        msg.setDelay(delay);
        msg.setStart(System.currentTimeMillis());
        msg.setSmd(cmd);
        return msg;
    }
}
