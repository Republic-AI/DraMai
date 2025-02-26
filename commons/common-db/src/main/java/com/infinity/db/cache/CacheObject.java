package com.infinity.db.cache;

import com.infinity.db.db.DBEntity;
import com.infinity.db.db.DBSqlType;
import com.infinity.db.util.ObjRef;

class CacheObject {
    private DBSqlType opt = DBSqlType.SELECT;
    private final DBEntity data;
    private int expire = -1;// -1表示永久
    private boolean invalid;
    private String lastMd5;
    private boolean dirty;
    private ObjRef<Integer> sz = new ObjRef<>(0);
    private int createMin;
    private long grabExpire;
    private boolean isReadonly;

    public boolean isGrabExpire() {
        return grabExpire <= System.currentTimeMillis();
    }

    public int getCreateMin() {
        return this.createMin;
    }

    public ObjRef<Integer> getSz() {
        return sz;
    }

    public CacheObject(final DBEntity data) {
        this.data = data;
        this.createMin = (int) (System.currentTimeMillis() / 60000);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public DBSqlType getOpt() {
        return opt;
    }

    public void setOpt(DBSqlType dbOpt, CacheStrategy strategy) {
        this.grabExpire = System.currentTimeMillis() + CacheChecker.interval + 1;
        this.setOpt(dbOpt, false);
        strategy.update(this);
    }

    public void setOpt(DBSqlType dbOpt, boolean i2u) {
        if (this.opt.ordinal() >= dbOpt.ordinal())
            return;
        if (!i2u && this.opt == DBSqlType.INSERT && dbOpt != DBSqlType.DELETE)
            return;
        this.opt = dbOpt;
    }

    public String getLastMd5() {
        return lastMd5;
    }

    public void setLastMd5(String lastMd5) {
        this.lastMd5 = lastMd5;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean isReadonly() {
        return this.isReadonly;
    }

    public DBEntity getData() {
        return data;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public void setReadonly(boolean readonly) {
        this.isReadonly = readonly;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CacheObject))
            return false;
        CacheObject oo = (CacheObject) o;
        return oo.getData().getClass() == this.getData().getClass() && oo.getData().getId() == this.getData().getId();
    }

    @Override
    public int hashCode() {
        return this.getData().getClass().hashCode() + Long.valueOf(this.getData().getId()).hashCode();
    }

}
