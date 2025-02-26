/*
package com.infinity.manager.task;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.infinity.common.Dummy;
import com.infinity.common.LoggerHelper;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeManager;
import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.ProtocolCommon;


public abstract class NotifyBaseTask<T> extends AbstractBaseTask {

    // 反射出的clz 对应method的缓存
    private static Map<String, Method> classMethodMap = new ConcurrentHashMap<>();

    protected boolean done = false;
    protected boolean parsed = false;

    protected T request;

    */
/**
     * 消息转 对象
     *
     * @param <R>
     * @return
     *//*

    @SuppressWarnings("unchecked")
    protected final <R> R msgToObj(ByteBuffer bb) {
        try {
            // 获取泛型类型
            Class<R> clz = (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];

            // 查看缓存中是否已经有 这个类的 method 对象;
            Method method = classMethodMap.get(clz.getName());
            if (method == null) {
                method = clz.getDeclaredMethod("parseFrom", ByteBuffer.class);
                classMethodMap.put(clz.getName(), method);
            }
            return (R) method.invoke(null, bb);

        } catch (Exception e) {
           LoggerHelper.error("msgToObj fail: %s", e.getMessage());
        }
        return null;
    }

    @Override
    public final void init() {
        if (parsed)
            return;
        parsed = parseRequest();
        if (!parsed) {
            done = true;
            LoggerHelper.error("parse request fail,cmd:%d", this.getCommandID());
        } else if (!init0()) {
            done = true;
            LoggerHelper.error("init fail,cmd:%d", this.getCommandID());
        }
    }

    @Override
    public final boolean run() {
        if (done) {
            return true;
        }
        done = run0();
        return done;
    }

    private boolean parseRequest() {
        if(((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0]==Dummy.class){
            return true;
        }   

        request = msgToObj(parsePrepareRequest());
        if (request == null) {
            return false;
        }
        return true;
    }

    */
/** 发给客户端 *//*

    protected final void sendMsg(byte[] data) {
        sendMsg(this.getCommandID(), data);
    }

    protected final void sendMsg(int cmd, byte[] data) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        sendMsg(cmd, thisHeader.getSource(), data);
    }

    protected final void sendMsg(int cmd, String dstId, byte[] data) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        sendMsg(cmd, dstId, thisHeader.getDestination(), data);
    }
    public final void sendMsg(IChannel channel,int cmd, String dstId, byte[] data) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        sendMsg(channel,false, ProtocolCommon.dstScope_Rand,false,cmd, dstId, thisHeader.getDestination(), data);
    }

    protected final void sendMsg(int cmd, String dstId, String srcId, byte[] data) {
        sendMsg(null,false, ProtocolCommon.dstScope_Rand, false, cmd, dstId, srcId, data);
    }


    */
/** 发给客户端 *//*

    public final void sendAll(int cmd, byte[] data) {
        List<INode> gatewayNodes = NodeManager.getInstance().getNodesWithType(NodeConstant.kGatewayService);
        HeaderOuterClass.Header thisHeader = this.getHeader();
        gatewayNodes.forEach(gateway -> {
            sendMsg(gateway.getChannel(),false, ProtocolCommon.dstScope_All, false, cmd, gateway.getNodeID(), thisHeader.getDestination(),
                    data);
        });
    }

    */
/* 跨服务器 *//*

    public final void sendCross(int cmd, String dstId, byte[] data) {
        sendCross(cmd, ProtocolCommon.dstScope_Rand,dstId,data);
    }
     */
/* 跨服务器 *//*

     protected final void sendCross(int cmd, String dstId, String srcId, byte[] data) {
        sendMsg(null,false, ProtocolCommon.dstScope_Rand, true, cmd, dstId, srcId, data);
    }
     */
/* 跨服务器 *//*

     public final void sendCross(int cmd,int dstScope, String dstId, byte[] data) {
        sendMsg(null,false, dstScope, true, cmd, dstId, NodeManager.getMyNodeID(), data);
    }
   

    protected final void sendMsg(IChannel channel,boolean direct, int dstScope, boolean request, int cmd, String dstId, String srcId,
            byte[] data) {
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(cmd);
        headerBuilder.setSource(srcId);
        headerBuilder.setDestination(dstId);
        headerBuilder.setRtype(request ? ProtocolCommon.kRequestTag : ProtocolCommon.kResponseTag);
        headerBuilder.setCode(0);
        headerBuilder.setDstScope(dstScope);
        //todo by zgf 0421
        */
/*if(channel==null){
            channel=(direct ?(
            NodeManager.getInstance().isPositive(dstId)?
            (Connector)NodeManager.getInstance().getPositiveNode(dstId).getConnector()
            : NodeManager.getInstance().getNode(dstId).getChannel()) 
            : this.getChannel());
        }*//*

       if(channel==null){
            LoggerHelper.error("sendMsg[%d]: channel is null",cmd);
            return;
       }
       channel.write(buildPacketBuffer(headerBuilder.build().toByteArray(), data));
    }

    protected boolean init0(){
        return true;
    }

    protected abstract boolean run0();

}
*/
