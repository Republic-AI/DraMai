package com.infinity.db.db;

public interface DBEntity<T> {
    long getId();

    void setId(long id);

    String getTableName();

    int getCacheExpire();// 缓存时长，分钟

    Class<T> ref_v();//实体类名

    void set_v(T _v);

    T get_v();//blob对应的实体类

    byte[] getV();//实体类对应的blob

    void setV(byte[] v);
}
