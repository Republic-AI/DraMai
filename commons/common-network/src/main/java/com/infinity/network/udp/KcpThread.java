/**
 * 维护kcp状态的线程
 */
package com.infinity.network.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class KcpThread extends Thread {
    private final Output out;
    private final LinkedBlockingQueue<DatagramPacket> inputs;
    private volatile boolean running;
    private final Map<InetSocketAddress, KcpOnUdp> kcps;
    private final KcpListener listener;
    private int nodelay;
    private int interval = Kcp.IKCP_INTERVAL;
    private int resend;
    private int nc;
    private int sndwnd = Kcp.IKCP_WND_SND;
    private int rcvwnd = Kcp.IKCP_WND_RCV;
    private int mtu = Kcp.IKCP_MTU_DEF;
    private boolean stream;
    private int minRto = Kcp.IKCP_RTO_MIN;
    private long timeout;//idle
    private final Object lock;//锁
    private final InetSocketAddress local;//本地地址

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
        this.nodelay = nodelay;
        this.interval = interval;
        this.resend = resend;
        this.nc = nc;
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.sndwnd = sndwnd;
        this.rcvwnd = rcvwnd;
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    /**
     * kcp工作线程
     *
     * @param out
     * @param listener
     * @param local
     */
    public KcpThread(Output out, KcpListener listener, InetSocketAddress local) {
        this.out = out;
        this.listener = listener;
        inputs = new LinkedBlockingQueue<>();
        kcps = new HashMap<>();
        this.lock = new Object();
        this.local = local;
    }

    /**
     * 开启线程
     */
    @Override
    public synchronized void start() {
        if (!this.running) {
            this.running = true;
            super.start();
        }
    }

    /**
     * 关闭线程
     */
    public void close() {
        this.running = false;
    }

    @Override
    public void run() {
        while (this.running) {
            long nowTime = System.currentTimeMillis();
            // input
            while (!this.inputs.isEmpty()) {
                DatagramPacket dp = this.inputs.remove();
                KcpOnUdp kcpOnUdp = this.kcps.get(dp.sender());
                ByteBuf content = dp.content();
                if (kcpOnUdp == null) {
                    // 初始化
                    kcpOnUdp = new KcpOnUdp(this.out, dp.sender(), local, this.listener);
                    kcpOnUdp.noDelay(nodelay, interval, resend, nc);
                    kcpOnUdp.wndSize(sndwnd, rcvwnd);
                    kcpOnUdp.setMtu(mtu);
                    // conv应该在客户端第一次建立时获取
                    int conv = content.getIntLE(0);
                    kcpOnUdp.setConv(conv);
                    kcpOnUdp.setMinRto(minRto);
                    kcpOnUdp.setStream(stream);
                    kcpOnUdp.setTimeout(timeout);
                    this.kcps.put(dp.sender(), kcpOnUdp);
                }
                kcpOnUdp.input(content);
            }

            // update
            KcpOnUdp closedKcpOnUdp = null;
            for (KcpOnUdp itemKcpOnUdp : this.kcps.values()) {
                if (itemKcpOnUdp.isClosed()) {
                    closedKcpOnUdp = itemKcpOnUdp;
                } else {
                    itemKcpOnUdp.update();
                }
            }

            // 删掉过时的kcp
            if (closedKcpOnUdp != null) {
                this.kcps.remove((InetSocketAddress) closedKcpOnUdp.getKcp().getUser());
            }

            // 如果输入为空则考虑wait
            if (inputs.isEmpty()) {
                long end = System.currentTimeMillis();
                if (end - nowTime < this.interval) {
                    synchronized (this.lock) {
                        try {
                            lock.wait(interval - end + nowTime);
                        } catch (InterruptedException e) {
                            log.error("wait the kcp thread. errorMsg={}", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        release();
    }

    /**
     * 收到输入
     */
    void input(DatagramPacket dp) {
        if (this.running) {
            this.inputs.add(dp);
            synchronized (this.lock) {
                lock.notify();
            }
        } else {
            dp.release();
        }
    }

    /**
     * stream mode
     */
    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public boolean isStream() {
        return stream;
    }

    public void setMinRto(int minRto) {
        this.minRto = minRto;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * 释放所有内存
     */
    private void release() {
        for (DatagramPacket dp : this.inputs) {
            dp.release();
        }
        this.inputs.clear();

        for (KcpOnUdp kcpOnUdp : this.kcps.values()) {
            if (!kcpOnUdp.isClosed()) {
                kcpOnUdp.release();
            }
        }
        this.kcps.clear();
    }
}

