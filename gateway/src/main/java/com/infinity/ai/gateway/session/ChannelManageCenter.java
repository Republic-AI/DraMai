package com.infinity.ai.gateway.session;

import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.common.SystemMsg;
import com.infinity.common.msg.platform.player.LogoutRequest;
import com.infinity.common.utils.MD5Utils;
import com.infinity.network.MessageSender;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManageCenter {
    final transient static Logger log = LoggerFactory.getLogger(ChannelManageCenter.class);
    private static ChannelManageCenter cmcIns = new ChannelManageCenter();
    //channel and connectSession mapping
    private DualHashBidiMap connectSessionMap;
    //channel and SessionId mapping,key=channel, value=sessionId
    private DualHashBidiMap tempCache;
    private ChannelGroup cg;
    private ConcurrentHashMap<Long, ConnectSession> sessionPool;
    public static final AttributeKey<ChannelAttach> akey = AttributeKey.valueOf("attach");

    private ChannelManageCenter() {
        connectSessionMap = new DualHashBidiMap();
        tempCache = new DualHashBidiMap();
        cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        sessionPool = new ConcurrentHashMap();

        //检测连接是否活动
        /*Threads.addListener(ThreadConst.TIMER_1S, "register#LoopChecker", new IntervalTimer(1, 60000) {
            @Override
            public boolean exec0(int interval) {
                checkIllegalChannel();
                return false;
            }
        });*/
    }

    public static ChannelManageCenter getInstance() {
        return cmcIns;
    }

    /****
     * 生成sesseion ID
     *
     * @param ch
     * @return
     */
    private String genSesseionId(Channel ch) {
        try {
            return MD5Utils.md5hex32(ch.hashCode() + "" + ch.remoteAddress()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkIllegalChannel() {
        if (cmcIns.tempCache.size() == 0) {
            return;
        }
        synchronized (cmcIns.tempCache) {
            try {
                Iterator<Object> ite = cmcIns.tempCache.mapIterator();
                while (ite.hasNext()) {
                    Object obj = ite.next();
                    Channel ch = (Channel) obj;
                    Attribute<ChannelAttach> attr = ch.attr(akey);
                    Object atta = attr.get();
                    if (atta != null && (atta instanceof ChannelAttach)) {
                        long time = System.currentTimeMillis();
                        ChannelAttach att = (ChannelAttach) atta;
                        if (time - att.getActivityTime() > 60 * 1000) {
                            log.info("非法链接在临时链接池持续超过60S:" + att.getChannel().remoteAddress() + "，断开连接!");
                            ch.close().sync();
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /**
     * 添加信道到临时缓存
     *
     * @param ch 连接通道
     * @return sessionId
     */
    public String addChannel(Channel ch) {
        String sessionId = genSesseionId(ch);
        Attribute<ChannelAttach> att = ch.attr(akey);
        if (att != null && att.get() != null) {
            att.get().setActivityTime(System.currentTimeMillis());
        } else {
            ChannelAttach ca = new ChannelAttach(ch, System.currentTimeMillis());
            att.setIfAbsent(ca);
        }
        tempCache.put(ch, sessionId);
        return sessionId;
    }

    /*public void addSessionId(String sessionId, Channel ch) {
        tempCache.putIfAbsent(ch, sessionId);
    }*/

    /***
     * get connectSession by channel
     *
     * @param ch
     * @return
     */
    public ConnectSession getSession(Channel ch) {
        if (cmcIns.connectSessionMap.containsKey(ch)) {
            ConnectSession session = (ConnectSession) cmcIns.connectSessionMap.get(ch);
            return session;
        }
        return null;
    }

    /***
     * get connectSession by channel
     *
     * @param ch
     * @return
     */
    public String getTempID(Channel ch) {
        if (cmcIns.tempCache.containsKey(ch)) {
            String session = (String) cmcIns.tempCache.get(ch);
            return session;
        }
        return null;
    }

    /***
     * remove the cached channel
     *
     * @param ch
     */
    public void removeTempSession(Channel ch) {
        if (cmcIns.tempCache.containsKey(ch)) {
            cmcIns.tempCache.remove(ch);
        }
    }

    public void removeSessionPool(Long userId) {
        if (cmcIns.sessionPool.containsKey(userId)) {
            cmcIns.sessionPool.remove(userId);
        }
    }

    public void removeChannel(Channel ch){
        cg.remove(ch);
    }

    /**
     * 绑定玩家与channel
     *
     * @param channelUid SessionId
     * @param userId     playerId
     * @return
     */
    public boolean bind(String channelUid, long userId) {
        log.debug("给userId:{}绑定session", userId);
        String SessionId = channelUid;
        if (cmcIns.tempCache.containsValue(SessionId)) {
            //Channel ch = (Channel) cmcIns.tempCache.removeValue(SessionId);
            Channel ch = (Channel) cmcIns.tempCache.getKey(SessionId);

            //退出guest用户
            ConnectSession guestSession = (ConnectSession) cmcIns.connectSessionMap.get(ch);
            if (guestSession != null && guestSession.getUserId() != userId) {
                removeSessionPool(guestSession.getUserId());
                clearSession(guestSession.getUserId());

                GameUserMgr.removeGameUser(guestSession.getUserId(), () -> {
                    LogoutRequest logoutRequest = new LogoutRequest();
                    logoutRequest.setPlayerId(guestSession.getUserId());
                    LogoutRequest.RequestData requestData = new LogoutRequest.RequestData();
                    requestData.setSourceServiceId(GatewayConfig.getInstance().getNodeId());
                    requestData.setType(0);
                    MessageSender.getInstance().broadcastMessageToAllService(logoutRequest);
                });
            }

            if (cmcIns.sessionPool.containsKey(userId)) {
                //该玩家session已存在，挤用户下线
                ConnectSession session = cmcIns.sessionPool.get(userId);
                Channel last = session.getChannel();
                if (last != null) {
                    if (last.equals(ch)) {
                        return false;
                    }

                    //别处登陆,挤用户下线,通知前端
                    last.writeAndFlush(new TextWebSocketFrame(SystemMsg.builder().build().toString()));
                    log.debug("用户别处登陆，被踢下线，IP={},playerId={}", last.remoteAddress(), userId);

                    session.setChannel(ch);
                    session.setSesseionId(channelUid);
                    cg.remove(last);
                    cg.add(ch);
                    cmcIns.connectSessionMap.remove(last);
                    cmcIns.connectSessionMap.put(ch, session);
                    last.disconnect();
                }
                return true;
            } else {
                cg.add(ch);
                ConnectSession session = new ConnectSession(channelUid, userId).setChannel(ch);
                cmcIns.connectSessionMap.put(ch, session);
                cmcIns.sessionPool.put(userId, session);
                log.info("userId:" + userId + "绑定session成功");
                return true;
            }
        } else {
            return false;
        }
    }

    public void clearSession(long userId) {
        ConnectSession session = cmcIns.sessionPool.remove(userId);
        cmcIns.connectSessionMap.removeValue(session);
    }

    public Channel getChannel(String sessionId) {
        if (cmcIns.tempCache.containsValue(sessionId)) {
            Object Object = cmcIns.tempCache.getKey(sessionId);
            return (Channel) Object;
        } else {

        }
        return null;
    }

    public ConnectSession getSession(long uid) {
        return cmcIns.sessionPool.get(uid);
    }

    public ChannelGroup getAllChannels() {
        return cg;

    }

    public int getConnectNum() {
        return this.tempCache.size();
    }
}
