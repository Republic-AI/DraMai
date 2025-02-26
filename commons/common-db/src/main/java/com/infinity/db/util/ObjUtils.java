package com.infinity.db.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ObjUtils {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }

    private static int max_serial_attamps = 5;

    /**
     * 序列化
     *
     * @param o
     * @return
     */
    public static <T> byte[] serial(T o) {
        if (o == null)
            return null;
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) o.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        Schema<T> schema = getSchema(clazz);
        int n = max_serial_attamps;
        while (n > 0) {
            try {
                return ProtostuffIOUtil.toByteArray(o, schema, buffer);
            } catch (Exception e) {
                --n;
                if (n <= 0) {
                    throw new ObjSerialException(
                            String.format("serial obj[%s] reach %s attempts", o.getClass().getSimpleName(),
                                    max_serial_attamps),
                            e.getCause());
                }
                try {
                    Thread.sleep(0);
                } catch (Exception ee) {
                    // ignore
                }
            } finally {
                buffer.clear();
            }
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bs
     * @param clazz
     * @return
     */
    public static <T> T deserial(byte[] bs, Class<T> clazz) {
        if (bs == null)
            return null;
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bs, obj, schema);
            return obj;
        } catch (Exception e) {
            log.error("{} deserial fail", clazz.getSimpleName());
            throw new ObjSerialException(e);
        }
    }

    public static String objectMd5(Object obj, ObjRef<Integer> sz) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] tmp = serial(obj);
        if (sz != null && tmp != null) {
            sz.obj = tmp.length;
        }
        md.update(tmp);
        return new BigInteger(1, md.digest()).toString(16);
    }

    public static String memprints(int prefixlen, String item, String pre, String after) {
        if (after == null) {
            return String.format("%" + (prefixlen + 1) + "s%s: [%s] ", " ", item,
                    pre);
        }
        return String.format("%" + (prefixlen + 1) + "s%s: [%s] -> [%s] ", " ", item,
                pre, after);

    }

    public static String memprints(int prefixlen, String item, long presz, String preunit, Long aftersz,
                                   String afterunit) {
        if (aftersz == null) {
            return String.format("%" + (prefixlen + 1) + "s%s: %s ", " ", item, memval(presz, preunit));
        }
        return String.format("%" + (prefixlen + 1) + "s%s: %s -> %s ", " ", item, memval(presz, preunit),
                memval(aftersz, afterunit));

    }

    public static String memval(long sz, String unit) {
        if (unit != null)
            return String.format("%s%s", sz, unit);
        unit = "b";
        float n = sz;
        if (sz >= 1024 * 1024 * 1024) {
            n /= 1024 * 1024 * 1024.0;
            unit = "g";
        } else if (sz >= 1024 * 1024) {
            n /= 1024 * 1024.0;
            unit = "m";
        } else if (sz >= 1024) {
            n /= 1024.0;
            unit = "k";
        }
        return String.format("%.2f%s", n, unit);
    }
}
