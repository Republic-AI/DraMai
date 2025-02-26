package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public abstract class AbstractBaseDataManager {
    private final String fileName;
    private final String basePath;

    protected AbstractBaseDataManager(final String basePath, final String fileName) {
        this.fileName = fileName;
        this.basePath = basePath;
    }

    protected final String getBasePath() {
        return basePath;
    }

    protected final String getFileName() {
        return fileName;
    }

    public abstract void decodeConfigObject(final String key, final JSONObject value);

    final protected void reload() {
        try {
            File configFile = new File(basePath, fileName);
            if (!configFile.exists()) {
                // 配置文件不存在，直接退出
                log.error("config file is not exists. pls check. path={}, fileName={}", basePath, fileName);
                System.exit(-1);
                return;
            }

            InputStream inputStream = new FileInputStream(configFile);
            String serviceJson = IOUtils.toString(inputStream, "UTF8");
            JSONArray allObject = JSON.parseArray(serviceJson);
            for(int i =0; i < allObject.size();i++){
                final JSONObject value = (JSONObject) allObject.get(i);
                decodeConfigObject(value.getString("id"), value);
            }

            afterReload();
        } catch (Exception e) {
            log.error("failed to load cfg[{}] data config.msg={}", fileName, e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected void afterReload() {

    }
}