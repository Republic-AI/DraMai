package com.infinity.common.base.thread;

class NameTask<T> {
    T task;
    String name;
    long createTm;

    NameTask(String name, T task) {
        this.name = name;
        this.task = task;
        this.createTm = System.currentTimeMillis();
    }

    T get() {
        return this.task;
    }
}
