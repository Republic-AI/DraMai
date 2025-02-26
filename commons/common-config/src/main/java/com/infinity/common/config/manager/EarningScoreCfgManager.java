package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.EarningScoreCfg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EarningScoreCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, EarningScoreCfg> config = new ConcurrentHashMap<>();

    public EarningScoreCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        EarningScoreCfg cfg = value.toJavaObject(EarningScoreCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public EarningScoreCfg get(final String parameter) {
        return config.get(parameter);
    }

    public EarningScoreCfg getCfgByCV(float cv) {
        Iterator<Map.Entry<Integer, EarningScoreCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, EarningScoreCfg> entry = iter.next();
            EarningScoreCfg config = entry.getValue();
            if (config.getIqStart() <= cv && (config.getIqEnd() <= 0 || config.getIqEnd() > cv)) {
                return config;
            }
        }
        return new EarningScoreCfg();
    }

    /**
     * 计算赚钱速率
     *
     * @param rate 先天数值
     * @param cv   亲密度
     * @return
     */
    public float getEarningRate(float rate, float cv) {
        EarningScoreCfg config = getCfgByCV(cv);
        return config == null ? 0 : new BigDecimal(config.getRate() * rate).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}