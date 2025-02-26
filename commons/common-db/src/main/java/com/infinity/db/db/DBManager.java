package com.infinity.db.db;

import com.infinity.db.cache.CacheManager;
import com.infinity.db.cache.CacheStrategy;
import com.infinity.db.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public class DBManager {
    private static DBopt dbRepo = null;
    private static Snowflake sf = null;

    /**
     * 要放在所有DB业务开启前
     */
    public static void init(ApplicationContext ac, int machineId, int maxn, boolean async, DBoptType dbType,
                            CacheStrategy strategy) {
        dbRepo = (DBopt) ac.getBean(dbType.getOpt());
        sf = new Snowflake(machineId);
        CacheManager.init(maxn, async, strategy);
        log.info("dbservice inited[{},{},{},{}]", machineId, async, dbType.name(), strategy.getClass().getSimpleName());
    }

    /**
     * 要放在所有DB业务结束后
     */
    public static void close() {
        CacheManager.close();
        log.info("dbservice closed");
    }

    public static DBopt get() {
        return dbRepo;
    }

    /**
     * 读写模式
     */
    @SuppressWarnings("unchecked")
    public static <T extends DBEntity> T get(Class<T> cls, long id) {
        return (T) CacheManager.get().get(cls, id, false);
    }

    /**
     * 只读模式
     */
    @SuppressWarnings("unchecked")
    public static <T extends DBEntity> T rget(Class<T> cls, long id) {
        return (T) CacheManager.get().get(cls, id, true);
    }

    /**
     * 立即入库
     * 只有即时性要求高的修改才需要使用此方法
     */
    public static void update(DBEntity data) {
        CacheManager.get().save(data, true);
    }

    /**
     * 立即入库
     */
    public static void add(DBEntity data) {
        if (data.getId() <= 0)
            data.setId(sf.nextId());
        CacheManager.get().save(data, false);
    }

    /**
     * 立即入库
     */
    public static <T extends DBEntity> void delete(Class<T> cls, long id) {
        CacheManager.get().delete(cls, id);
    }
}
