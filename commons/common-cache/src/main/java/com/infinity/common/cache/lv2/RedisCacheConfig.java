package com.infinity.common.cache.lv2;

import com.infinity.common.utils.LoadPropertiesFileUtil;

import java.util.Properties;

public class RedisCacheConfig {
    private static String url;
    private static int port;
    private static int database;
    private static String pwd;

    static {
        init();
    }

    public static void init() {
        //Properties p = LoadPropertiesFileUtil.loadProperties("../config/cfg.properties");
        Properties p = LoadPropertiesFileUtil.loadProperties("config/config.properties");
        port = Integer.parseInt(p.getProperty("base.cache.port"));
        url = p.getProperty("base.cache.url");
        database = Integer.parseInt(p.getProperty("base.cache.dbid"));
        pwd = p.getProperty("base.cache.pwd");
        if (pwd == null) {
            pwd = "";
        }
    }

    public static String getUrl() {
        return url;
    }

    public static int getPort() {
        return port;
    }

    public static int getDatabase() {
        return database;
    }

    public static String getPwd() {
        return pwd;
    }

}
