package com.infinity.task;

public interface ITaskCreator {
    int command();

    ITask create();
}