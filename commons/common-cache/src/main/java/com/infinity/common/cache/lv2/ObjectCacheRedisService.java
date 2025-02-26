package com.infinity.common.cache.lv2;

import com.infinity.common.cache.CacheObj;
import com.infinity.common.utils.GsonUtil;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectCacheRedisService /*implements ObjectCacheService*/ {

    public static boolean hset(String key, String field, String value) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            long result = ((Jedis) redis).hset(key, field, value);
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return false;
    }

    public static boolean hdel(String key, String[] field) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            long result = ((Jedis) redis).hdel(key, field);
            if (result >= 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return false;
    }

    public static Map<String,String> hgetAll(String key) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            Map<String,String> result = ((Jedis) redis).hgetAll(key);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return null;
    }

    /**
     * 移除单个对象
     */
    public static boolean remove(String key) {
        // log.info("redis remove "+key);
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            long result = redis.del(key.getBytes());
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return false;
    }

    public static <T extends CacheObj> boolean saveObject(String key, T object) {
        return set(RedisCacheConfig.getDatabase(), key, object);
    }

    public static <T extends CacheObj> T get(String key, Class<T> c) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            byte[] data = redis.get(encode(key));
            return decode(data, c);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            redis.close();
        }
    }

    /**
     * 模糊查找
     *
     * @param dbid
     * @param key
     * @param c
     * @return
     */
    public static <T extends CacheObj> Set<T> fuzzyKeys(String key, Class<T> c) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            Set<byte[]> keys = redis.keys(encode(key));
            if (keys != null && keys.size() > 0) {
                Set<T> objs = new HashSet<T>();
                for (byte[] bs : keys) {
                    objs.add(decode(redis.get(bs), c));
                }
                return objs;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            redis.close();
        }
    }

    public static void fuzzyKeysDel(String key) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            Set<byte[]> keys = redis.keys(encode(key));
            if (keys != null && keys.size() > 0) {

                for (byte[] bs : keys) {
                    redis.del(bs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
    }

    public static void expire(String key, int expire) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(RedisCacheConfig.getDatabase());
            redis.expire(encode(key), expire);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
    }

    private static <T extends CacheObj> boolean set(int dbid, String key, T object) {
        BinaryJedis redis = null;
        try {
            redis = JedisPoolHelper.getInstance().getPool().getResource();
            redis.select(dbid);
            redis.set(encode(key), encode(object));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redis.close();
        }
        return false;
    }

    private static <T extends CacheObj> T decode(byte[] bytes, Class<T> c) {
        T t = null;
        Exception thrown = null;
        try {
            t = (T) GsonUtil.parseJson(toStr(bytes), c);
        } catch (ClassCastException e) {
            thrown = e;
        } finally {
            if (null != thrown)
                throw new RuntimeException("Error decoding byte[] data to instantiate java object - "
                        + "data at key may not have been of this type or even an object", thrown);
        }
        return t;
    }

    private static final String toStr(byte[] bytes) {
        String str = null;
        if (null != bytes) {
            try {
                str = new String(bytes, SUPPORTED_CHARSET_NAME);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return str;
        // return new String(bytes, SUPPORTED_CHARSET); // Java 1.6 only
    }

    private static final byte[] encode(String value) {
        byte[] bytes = null;
        try {
            bytes = value.getBytes(SUPPORTED_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
        }
        return bytes;
        // return value.getBytes(SUPPORTED_CHARSET);
    }

    private static <T extends CacheObj> byte[] encode(T obj) {
        byte[] bytes = null;
        try {
            bytes = GsonUtil.parseObject(obj).getBytes(SUPPORTED_CHARSET_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing object" + obj + " => " + e);
        }
        return bytes;
    }

    private final static String SUPPORTED_CHARSET_NAME = "UTF-8";
}
