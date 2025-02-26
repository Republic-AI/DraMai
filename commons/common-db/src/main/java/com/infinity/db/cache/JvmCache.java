package com.infinity.db.cache;

import com.infinity.db.db.DBEntity;
import com.infinity.db.db.DBManager;
import com.infinity.db.db.DBSqlType;
import com.infinity.db.except.DBServiceException;
import com.infinity.db.util.ObjUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JvmCache {
    private Map<Class<?>, Map<Long, CacheObject>> tbcaches = new ConcurrentHashMap<>();
    private boolean async;
    private CacheStrategy strategy;

    public JvmCache(boolean async, CacheStrategy strategy) {
        this.async = async;
        this.strategy = strategy;
    }

    public DBEntity get(Class<?> cls, long id, boolean readonly) {
        Map<Long, CacheObject> tbcache = this.getAll0(cls);
        CacheObject co = tbcache.get(id);
        if (co != null && co.isDirty())
            throw new DBServiceException(String.format("%s[%s] is dirty", cls, id));

        if (co == null) {
            DBEntity data = DBManager.get().get(cls, id);
            if (data == null)
                return null;

            co = addCache0(tbcache, data, DBSqlType.UPDATE);
        } else {
            co.setOpt(DBSqlType.UPDATE, strategy);
        }

        //只读模式
        co.setReadonly(readonly);

        if (co != null) {
            return co.getData();
        }
        return null;
    }

    public void save(DBEntity data, boolean update) {
        long id = data.getId();
        if (id <= 0)
            throw new DBServiceException(String.format("[%s] has illegal id[%s]", data.getTableName(), id));

        Map<Long, CacheObject> tbcache = this.getAll0(data.getClass());
        CacheObject co = tbcache.get(id);
        if (co != null && co.isDirty())
            throw new DBServiceException(String.format("%s[%s] is dirty", data.getTableName(), id));

        if (co != null && co.isReadonly())
            throw new DBServiceException(String.format("use the method 'get' instead of 'rget' before change %s:%d",
                    data.getTableName(), id));

        if (!async) {
            try {
                DBManager.get().save(data, update);
            } catch (Exception e) {
                throw new DBServiceException(e.getMessage());
            }

            if (co == null) {
                co = addCache0(tbcache, data, DBSqlType.UPDATE);
            }
        } else {
            if (co == null) {
                co = addCache0(tbcache, data, update ? DBSqlType.UPDATE : DBSqlType.INSERT);
            }

            if (co != null) {
                CacheManager.getWorker(data.getId()).immediate(co);
            }
        }
    }

    public void delete(Class<?> cls, long id) {
        if (!async) {
            try {
                DBManager.get().delete(cls, id);
            } catch (Exception e) {
                throw new DBServiceException(e.getMessage());
            }
        }

        CacheObject co = this.expire(cls, id);
        if (co != null) {
            if (async) {
                co.setOpt(DBSqlType.DELETE, strategy);
                CacheManager.getWorker(id).immediate(co);
            } else {
                co.setDirty();
            }
        }
    }

    CacheObject invalid(Class<?> cls, long id) {
        Map<Long, CacheObject> tbcache = this.getAll0(cls);
        CacheObject co = tbcache.get(id);
        if (co != null && co.isGrabExpire()) {
            tbcache.remove(id);
            co.setInvalid(true);
        }
        return co;
    }

    public CacheObject expire(Class<?> cls, long id) {
        CacheObject co = this.getAll0(cls).get(id);
        if (co != null) {
            co.setExpire(0);
        }
        return co;
    }

    private CacheObject addCache0(Map<Long, CacheObject> tbcache, DBEntity data, DBSqlType dbOpt)
            throws DBServiceException {
        if (data.getCacheExpire() == 0)
            return null;

        CacheObject tmp = tbcache.get(data.getId());
        if (tmp != null)
            return tmp;

        CacheObject co = new CacheObject(data);
        co.setOpt(dbOpt, strategy);

        try {
            co.setLastMd5(ObjUtils.objectMd5(co.getData(), co.getSz()));
        } catch (Exception e) {
            throw new DBServiceException(String.format("md5 %s:%d fail", data.getTableName(), data.getId()));
        }

        tmp = tbcache.putIfAbsent(data.getId(), co);
        if (tmp != null)
            return tmp;

        CacheManager.getWorker(data.getId()).add(co);
        return co;
    }

    public Map<Class<?>, Map<Long, CacheObject>> getAll() {
        return this.tbcaches;
    }

    private Map<Long, CacheObject> getAll0(Class<?> cls) {
        makesureTB(cls);
        return this.tbcaches.get(cls);
    }

    private void makesureTB(Class<?> cls) {
        if (!tbcaches.containsKey(cls)) {
            tbcaches.putIfAbsent(cls, new ConcurrentHashMap<>());
        }
    }
}