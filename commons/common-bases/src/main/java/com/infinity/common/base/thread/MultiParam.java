package com.infinity.common.base.thread;

import java.util.ArrayList;
import java.util.List;

public class MultiParam<T> {
    private List<T> ps = new ArrayList<>();

    public MultiParam<T> append(T t) {
        ps.add(t);
        return this;
    }

    public List<T> params() {
        return this.ps;
    }
}
