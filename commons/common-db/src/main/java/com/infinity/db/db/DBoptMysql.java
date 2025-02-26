package com.infinity.db.db;

import com.infinity.db.except.DBDirtyException;
import com.infinity.db.except.DBServiceException;
import com.infinity.db.util.ObjUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DBoptMysql implements DBopt {
    private final JdbcTemplate jdbcTemplate;
    private final String insertSql = "insert into %s (%s) values (%s)";
    private final String updateSql = "update %s set %s where id=?";
    private final String deleteSql = "delete from %s where id=?";
    private final String selectSql = "select * from %s where id=?";
    private Map<Class<?>, DBSql> tbsqls = new HashMap<>();

    private void makesureSqls(Class<?> cls) throws Exception {
        if (!tbsqls.containsKey(cls)) {
            DBSql sqls = new DBSql();
            StringBuffer insertCols = new StringBuffer();
            StringBuffer insertValues = new StringBuffer();
            StringBuffer updateCols = new StringBuffer();
            Object o = cls.getDeclaredConstructor().newInstance();
            String tbName = cls.getMethod("getTableName").invoke(o).toString();
            for (Field f : cls.getDeclaredFields()) {
                if (f.getName().equals("_v"))
                    continue;

                if (insertCols.length() != 0) {
                    insertCols.append(",");
                    insertValues.append(",");
                }

                insertCols.append(f.getName());
                insertValues.append("?");
                if (!"id".equals(f.getName())) {
                    if (updateCols.length() != 0)
                        updateCols.append(",");

                    updateCols.append(f.getName() + "=?");
                }
            }

            sqls.setInsert(String.format(insertSql, tbName, insertCols.toString(), insertValues.toString()));
            sqls.setUpdate(String.format(updateSql, tbName, updateCols.toString()));
            sqls.setDelete(String.format(deleteSql, tbName));
            sqls.setSelect(String.format(selectSql, tbName));
            tbsqls.put(cls, sqls);
        }
    }

    public DBoptMysql(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DBEntity get(Class<?> cls, long id) {
        try {
            makesureSqls(cls);
            DBEntity rt = (DBEntity) jdbcTemplate.queryForObject(tbsqls.get(cls).getSelect(), new BeanPropertyRowMapper<>(cls), id);
            if (rt != null && rt.ref_v() != null) {
                Object o = ObjUtils.deserial(rt.getV(), rt.ref_v());
                rt.set_v(o);
                rt.setV(null);
            }
            return rt;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(DBEntity data, boolean update) throws DBDirtyException {
        try {
            Class<?> cls = data.getClass();
            makesureSqls(cls);

            Field[] fields = cls.getDeclaredFields();
            List<Object> args = new ArrayList<>();
            if (data.ref_v() != null) {
                data.setV(ObjUtils.serial(data.get_v()));
            }

            for (int i = 0; i < fields.length; i++) {
                if ("id".equals(fields[i].getName())) {
                    if (!update) {
                        if (data.getId() <= 0)
                            throw new DBServiceException(String.format("[%s] has illegal id[%s]", data.getTableName(), data.getId()));
                        args.add(data.getId());
                    }
                    continue;
                } else if ("_v".equals(fields[i].getName())) {
                    continue;
                }

                fields[i].setAccessible(true);
                args.add(fields[i].get(data));
            }

            if (update)
                args.add(data.getId());

            int n = jdbcTemplate.update(update ? tbsqls.get(cls).getUpdate() : tbsqls.get(cls).getInsert(), args.toArray());
            if (n != 1) {
                throw new DBServiceException(
                        String.format("save[%s:%d] fail with %s records", data.getTableName(), data.getId(), n));
            }
        } catch (Exception e) {
            throw new DBDirtyException(
                    String.format("%s[%s] is dirty, %s", data.getTableName(), data.getId(), e.getMessage()), e.getCause());
        } finally {
            data.setV(null);
        }
    }

    @Override
    public void delete(Class<?> cls, long id) throws DBDirtyException {
        try {
            makesureSqls(cls);
            int n = jdbcTemplate.update(tbsqls.get(cls).getDelete(), id);
            if (n != 1) {
                throw new DBServiceException(String.format("delete[%s:%d] fail with %s records", cls, id, n));
            }
        } catch (Exception e) {
            throw new DBDirtyException(String.format("%s[%s] is dirty, %s", cls, id, e.getMessage()), e.getCause());
        }
    }
}
