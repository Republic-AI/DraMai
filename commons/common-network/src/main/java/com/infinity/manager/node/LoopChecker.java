package com.infinity.manager.node;

import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.Threads;
import com.infinity.network.NetThreadConst;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoopChecker implements ICheckerManager {
    private BlockingQueue<IChecker> checkers_;

    private boolean exit_ = false;

    public LoopChecker() {
        checkers_ = new LinkedBlockingQueue<IChecker>();
        Threads.regist(new NameParam(NetThreadConst.TIMER_NET_LoopChecker, 1, 50000));
        Threads.addListener(NetThreadConst.TIMER_NET_LoopChecker, "network#LoopChecker", this::onLoop);
    }

    public void start() {
        exit_ = false;
    }

    public void end() {
        exit_ = true;
        checkers_.clear();

        checkers_ = null;
    }

    private boolean onLoop(int interval) {
        if (exit_) return true;

        for (IChecker checker : checkers_) {
            checker.update();
            if (exit_)
                break;
        }
        return false;
    }

    @Override
    public void add(IChecker checker) {
        if (!checkers_.contains(checker))
            checkers_.add(checker);
    }

    @Override
    public void remove(IChecker checker) {
        checkers_.remove(checker);
    }

    public boolean contains(IChecker checker) {
        return checkers_ != null && checkers_.contains(checker);
    }
}