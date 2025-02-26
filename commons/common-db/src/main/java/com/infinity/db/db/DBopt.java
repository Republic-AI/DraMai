package com.infinity.db.db;

import com.infinity.db.except.DBDirtyException;

public interface DBopt {
    DBEntity get(Class<?> cls, long id);

    void save(DBEntity data, boolean update) throws DBDirtyException;

    void delete(Class<?> cls, long id) throws DBDirtyException;
}