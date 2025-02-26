/*
 * udp for kcp
 */
package com.infinity.network.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class KcpOnUdp {
    //kcp的状态
    private final Kcp kcp;
    //输入
    private final Queue<ByteBuf> received;
    private final Queue<ByteBuf> sendList;
    //超时设定
    private long timeout;
    //上次超时检查时间
    private long lastTime;
    //错误代码
    private int errorCode;
    private String sessionId;

    private final KcpListener listener;
    private volatile boolean needUpdate;
    private volatile boolean closed;

    private final Map<Object, Object> session;

    //本地
    private final InetSocketAddress local;
    //远程地址
    private final InetSocketAddress remote;
    private static long max_reces = 0L;
    private static long max_sends = 0L;
    private long reces = 0L;
    private long sends = 0L;
    private long st = System.currentTimeMillis();

    public int getLastErrorCode() {
        return errorCode;
    }

    /**
     * kcp for udp
     *
     * @param out      输出接口
     * @param remote   远程地址
     * @param local    本地地址
     * @param listener 监听
     */
    public KcpOnUdp(Output out, InetSocketAddress remote, InetSocketAddress local, KcpListener listener) {
        this.kcp = new Kcp(out, remote);

        this.session = new ConcurrentHashMap<>();
        this.received = new LinkedBlockingQueue<>();
        this.sendList = new LinkedBlockingQueue<>();

        this.local = local;
        this.remote = remote;
        this.listener = listener;

        this.closed = false;
        this.needUpdate = false;
        this.errorCode = 0;
    }

    /**
     * fastest: ikcp_nodelay(kcp, 1, 20, 2, 1) nodelay: 0:disable(default),
     * 1:enable interval: internal update timer interval in millisec, default is
     * 100ms resend: 0:disable fast resend(default), 1:enable fast resend nc:
     * 0:normal congestion control(default), 1:disable congestion control
     *
     * @param nodelay
     * @param interval
     * @param resend
     * @param nc
     */
    public void noDelay(int nodelay, int interval, int resend, int nc) {
        this.kcp.noDelay(nodelay, interval, resend, nc);
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.kcp.wndSize(sndwnd, rcvwnd);
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.kcp.setMtu(mtu);
    }

    /**
     * conv
     *
     * @param conv
     */
    public void setConv(int conv) {
        this.kcp.setConv(conv);
    }

    /**
     * stream模式
     *
     * @param stream
     */
    public void setStream(boolean stream) {
        this.kcp.setStream(stream);
    }

    /**
     * 流模式
     *
     * @return
     */
    public boolean isStream() {
        return this.kcp.isStream();
    }

    /**
     * rto设置
     *
     * @param rto
     */
    public void setMinRto(int rto) {
        this.kcp.setMinRto(rto);
    }

    /**
     * send data to addr
     *
     * @param byteBuffer
     */
    public void send(ByteBuf byteBuffer) {
        if (!closed) {
            sends += byteBuffer.readableBytes();
            this.sendList.add(byteBuffer);
            this.needUpdate = true;
        } else {
            byteBuffer.release();
        }
    }

    /**
     * update one kcp
     */
    public void update() {
        // input
        while (!this.received.isEmpty()) {
            ByteBuf dp = this.received.remove();
            errorCode = kcp.input(dp);
            dp.release();
            if (errorCode != 0) {
                this.closed = true;
                this.release();
                this.listener.handleException(new IllegalStateException("input error : " + errorCode), this);
                this.listener.handleClose(this);
                return;
            }
        }

        // receive
        int nLength;
        while ((nLength = kcp.peekSize()) > 0) {
            ByteBuf content = PooledByteBufAllocator.DEFAULT.buffer(nLength);
            int nResult = kcp.receive(content);
            if (nResult > 0) {
                this.listener.handleReceive(content, this);
            } else {
                content.release();
            }
        }

        // send
        while (!this.sendList.isEmpty()) {
            ByteBuf sendContent = sendList.remove();
            errorCode = this.kcp.send(sendContent);
            sendContent.release();
            if (errorCode != 0) {
                this.closed = true;
                this.release();
                this.listener.handleException(new IllegalStateException("send error : " + errorCode), this);
                this.listener.handleClose(this);
                return;
            }
        }

        // update kcp status
        if (this.needUpdate) {
            kcp.flush();
            this.needUpdate = false;
        }

        long cur = System.currentTimeMillis();
        if (cur >= kcp.getNextUpdate()) {
            kcp.update(cur);
            kcp.setNextUpdate(kcp.check(cur));
        }

        // check timeout
        if (this.timeout > 0 && lastTime > 0 && System.currentTimeMillis() - lastTime > this.timeout) {
           /*  this.closed = true;
            this.release();
            this.listener.handleClose(this); */
            this.close();
        }
    }

    /**
     * 输入
     *
     * @param content
     */
    public void input(ByteBuf content) {
        if (!this.closed) {
            reces += content.readableBytes();
            this.received.add(content);
            this.needUpdate = true;
            this.lastTime = System.currentTimeMillis();
        } else {
            content.release();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        if (sends > max_sends) {
            max_sends = sends;
        }
        if (reces > max_reces) {
            max_reces = reces;
        }
        log.debug("close: udp sz: [{}] [reces:{}KB,sends:{}KB,costs:{}s,max_reces:{}KB,max_sends:{}KB]", this.getSessionId(), reces / 1024, sends / 1024, (System.currentTimeMillis() - st) / 1000, max_reces / 1024, max_sends / 1024);
        this.closed = true;
        this.release();
        this.listener.handleClose(this);
    }

    public Kcp getKcp() {
        return kcp;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "local: " + local + " remote: " + remote;
    }

    /**
     * session id
     *
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * session id
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * session map
     *
     * @return
     */
    public Map<Object, Object> getSessionMap() {
        return session;
    }

    /**
     * session k v
     *
     * @param k
     * @return
     */
    public Object getSession(Object k) {
        return this.session.get(k);
    }

    /**
     * session k v
     *
     * @param k
     * @param v
     * @return
     */
    public Object setSession(Object k, Object v) {
        return this.session.put(k, v);
    }

    /**
     * contains key
     *
     * @param k
     * @return
     */
    public boolean containsSessionKey(Object k) {
        return this.session.containsKey(k);
    }

    /**
     * contains value
     *
     * @param v
     * @return
     */
    public boolean containsSessionValue(Object v) {
        return this.session.containsValue(v);
    }

    /**
     * 立即更新？
     *
     * @return
     */
    boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 监听器
     *
     * @return
     */
    public KcpListener getListener() {
        return listener;
    }

    /**
     * 本地地址
     *
     * @return
     */
    public InetSocketAddress getLocal() {
        return local;
    }

    /**
     * 远程地址
     *
     * @return
     */
    public InetSocketAddress getRemote() {
        return remote;
    }

    /**
     * 释放内存
     */
    void release() {
        this.kcp.release();

        for (ByteBuf item : this.received) {
            item.release();
        }
        this.received.clear();

        for (ByteBuf item : this.sendList) {
            item.release();
        }
        this.sendList.clear();
    }
}
