package com.infinity.db.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FixedPriorityQueue<T> {
    private List<T> ll;
    private Comparator<T> cmp;
    private int capacity;

    public FixedPriorityQueue(Comparator<T> cmp, int capacity) {
        this.cmp = cmp;
        this.capacity = capacity;
        this.ll = new ArrayList<>(this.capacity);
    }

    public void add(T t) {
        if (ll.size() == 0) {
            ll.add(t);
            return;
        }
        int idx = 0;
        for (T tmp : ll) {
            if (cmp.compare(t, tmp) <= 0) {
                break;
            }
            idx += 1;
        }
        if (ll.size() == this.capacity) {
            ll.remove(this.capacity - 1);
        }
        if (idx >= ll.size()) {
            ll.add(t);
        } else {
            ll.add(idx, t);
        }
    }

    public Iterator<T> iterator() {
        return this.ll.iterator();
    }

    public int size() {
        return this.ll.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[ ");
        for (T t : this.ll) {
            sb.append(t.toString()).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public void clear() {
        this.ll.clear();
    }

    public T get(int idx) {
        return this.ll.get(idx);
    }
}
