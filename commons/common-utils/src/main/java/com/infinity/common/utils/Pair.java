package com.infinity.common.utils;

public class Pair<T1, T2> implements Comparable<Pair<T1, T2>>
{
    public Pair(T1 t1, T2 t2)
    {
        first = t1;
        second = t2;
    }

    public final T1 first;
    public final T2 second;

    @Override
    public boolean equals(Object obj)
    {
        Pair<T1, T2> cmpPair = (Pair<T1, T2>) (obj);
        return cmpPair != null && (cmpPair.first == this.first && cmpPair.second == this.second);
    }


    @Override
    public int compareTo(Pair<T1, T2> o)
    {
        if (o == null)
            return 1;
        int ret = ((Comparable<T1>) this.first).compareTo(o.first);
        if (ret != 0)
            return ret;
        return ((Comparable<T2>) this.second).compareTo(o.second);
    }

    @Override
    public String toString()
    {
        if (this.first == null)
        {
            if (this.second == null)
                return "null";
            return this.second.toString();
        }
        return this.first + "," + (this.second == null ? "null" : this.second.toString());
    }
}
