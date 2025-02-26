package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.EarningCVCfg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EarningCVCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, EarningCVCfg> config = new ConcurrentHashMap<>();

    public EarningCVCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        EarningCVCfg cfg = value.toJavaObject(EarningCVCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public EarningCVCfg get(final String parameter) {
        return config.get(parameter);
    }

    public EarningCVCfg getCfgByCV(float cv) {
        Iterator<Map.Entry<Integer, EarningCVCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, EarningCVCfg> entry = iter.next();
            EarningCVCfg config = entry.getValue();
            if (config.getIqStart() <= cv && (config.getIqEnd() <= 0 || config.getIqEnd() > cv)) {
                return config;
            }
        }
        return null;
    }

    /**
     * 计算赚钱速率
     * @param rate 先天数值
     * @param cv 亲密度
     * @return
     */
    public float getEarningRate(float rate, float cv) {
        EarningCVCfg config = getCfgByCV(cv);
        return config == null ? 0 : new BigDecimal(config.getRate() * rate).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}