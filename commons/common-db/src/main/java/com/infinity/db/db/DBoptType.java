package com.infinity.db.db;

public enum DBoptType {

    MYSQL(DBoptMysql.class);

    private Class<?> opt;

    DBoptType(Class<?> opt) {
        this.opt = opt;
    }

    public Class<?> getOpt() {
        return this.opt;
    }
}
