package com.infinity.common.base.game;

//游戏启动器
public interface IGameStarter {
    void start();

    void run();

    void exit(String reason);
}
