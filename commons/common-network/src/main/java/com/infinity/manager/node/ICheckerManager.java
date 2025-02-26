package com.infinity.manager.node;

public interface ICheckerManager {
    void add(IChecker checker);

    void remove(IChecker checker);

    boolean contains(IChecker checker);
}