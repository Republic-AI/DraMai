package com.infinity.db.util;

public class Tuple<T1, T2, T3> {
    public T1 first;
    public T2 second;
    public T3 third;

    public Tuple(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String toString() {
        return String.format("[%s,%s,%s]", first, second, third);
    }
}
