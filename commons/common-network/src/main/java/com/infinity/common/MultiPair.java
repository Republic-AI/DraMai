package com.infinity.common;

import java.util.ArrayList;
import java.util.List;

public class MultiPair<K, V> {
    private List<Pair<K, V>> ps = new ArrayList<>();

    public MultiPair<K, V> append(K k, V v) {
        this.ps.add(new Pair<>(k, v));
        return this;
    }

    public List<Pair<K, V>> pairs() {
        return this.ps;
    }
}
