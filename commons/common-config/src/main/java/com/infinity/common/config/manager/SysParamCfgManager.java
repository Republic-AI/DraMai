package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.SysParamCfg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SysParamCfgManager extends AbstractBaseDataManager {
    private final Map<String, SysParamCfg> config = new ConcurrentHashMap<>();

    public SysParamCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        int id = Integer.parseInt(key);
        SysParamCfg cfg = value.toJavaObject(SysParamCfg.class);
        config.put(cfg.getParameter(), cfg);
    }

    public SysParamCfg get(final String parameter) {
        return config.get(parameter);
    }

    public String getParameterValue(final String parameter) {
        SysParamCfg sysParamCfg = get(parameter);
        return sysParamCfg == null ? null : sysParamCfg.getValue();
    }

    public String getParameterValue(final String parameter, String defaultValue) {
        SysParamCfg sysParamCfg = get(parameter);
        return sysParamCfg == null ? defaultValue : sysParamCfg.getValue();
    }

    public int getParameterInt(final String parameter, int defaultValue) {
        SysParamCfg sysParamCfg = get(parameter);
        return sysParamCfg == null ? defaultValue : Integer.parseInt(sysParamCfg.getValue());
    }
}