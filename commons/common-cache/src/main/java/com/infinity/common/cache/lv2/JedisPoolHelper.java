package com.infinity.common.cache.lv2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolHelper {
    final transient static Logger log = LoggerFactory.getLogger(JedisPoolHelper.class);

    private JedisPoolHelper() {
        JedisPoolConfig config = new JedisPoolConfig();// Jedis池配置
        config.setMaxTotal(500); // 最大活动的对象个数
        config.setMaxIdle(1000 * 60);// 对象最大空闲时间
        config.setMaxWaitMillis(1000 * 3);// 获取对象时最大等待时间
        String url = RedisCacheConfig.getUrl();
        int port = RedisCacheConfig.getPort();
        int database = RedisCacheConfig.getDatabase();
        String passwd = RedisCacheConfig.getPwd();
        if (passwd.equals("")) {
            cachedPool = new JedisPool(config, url, port);
        } else {
            cachedPool = new JedisPool(config, url, port, 30, passwd);
        }
        log.info(cachedPool.getResource().ping());
        log.info(" * connect CacheObjManager cache " + url + " db " + database);

    }

    private static JedisPoolHelper ins = new JedisPoolHelper();
    private JedisPool cachedPool;

    public static JedisPoolHelper getInstance() {
        return ins;
    }

    public JedisPool getPool() {
        return cachedPool;
    }

}
