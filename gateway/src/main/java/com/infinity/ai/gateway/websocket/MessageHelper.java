package com.infinity.ai.gateway.websocket;

import com.infinity.ai.gateway.session.ChannelManageCenter;
import com.infinity.ai.gateway.session.ConnectSession;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtoHelper;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.network.ManagerService;
import com.infinity.task.ITask;
import com.infinity.task.ITaskFactory;
import com.infinity.task.ITaskManager;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHelper {
    final transient static Logger log = LoggerFactory.getLogger(MessageHelper.class);

    public static void dispatchMsg(BaseMsg msg) {
        ITaskFactory taskFactory = ManagerService.getTaskFactory();
        if (taskFactory != null) {
            ITask newITask = taskFactory.createTask(ProtocolCommon.MSG_DISPATCH, null, null, null, msg);
            if (newITask != null) {
                ITaskManager taskManager = ManagerService.getTaskManager();
                taskManager.add(newITask);
            }
        }
    }

    //gateway -> 客户端
    public static void outMsgToClient(BaseMsg msg) {
        log.info("send to client:{}", msg);
        Channel channel = null;
        ConnectSession session = ChannelManageCenter.getInstance().getSession(msg.getPlayerId());
        if (session != null)
            channel = session.getChannel();

        if (channel == null) {
            channel = ChannelManageCenter.getInstance().getChannel(msg.getSessionId());
        }

        if (channel == null) {
            channel = ChannelManageCenter.getInstance().getChannel(GameUserMgr.getGameUser(msg.getPlayerId()).getSessionId());
        }

        outMsgToClient(msg, channel);
    }

    public static void outMsgToClient(BaseMsg msg, Channel channel) {
        msg.clear();
        String object = ProtoHelper.parseObject(msg);
        outMsgToClient(object, channel);
    }

    public static void outMsgToClient(String msg, Channel channel) {
        if (channel != null) {
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg);
            channel.writeAndFlush(textWebSocketFrame);
        }
    }
}
