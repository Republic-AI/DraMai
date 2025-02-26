package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.EarningIQCfg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EarningIQCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, EarningIQCfg> config = new ConcurrentHashMap<>();

    public EarningIQCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        EarningIQCfg cfg = value.toJavaObject(EarningIQCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public EarningIQCfg get(final String parameter) {
        return config.get(parameter);
    }

    public EarningIQCfg getCfgByIQ(int iq) {
        /*return config.values().stream()
                .sorted(Comparator.comparing(EarningIQCfg::getIqStart))
                .filter(c -> (c.getIqStart() <= iq && (c.getIqEnd() <= 0 || c.getIqEnd() < iq)))
                .findFirst()
                .orElse(null);*/

        Iterator<Map.Entry<Integer, EarningIQCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, EarningIQCfg> entry = iter.next();
            EarningIQCfg config = entry.getValue();
            if (config.getIqStart() <= iq && (config.getIqEnd() <= 0 || config.getIqEnd() > iq)) {
                return config;
            }
        }
        return null;
    }

    public float getEarningRate(int iq) {
        EarningIQCfg config = getCfgByIQ(iq);
        //return config == null ? 0 : new BigDecimal(config.getRate() * iq).setScale(2, RoundingMode.HALF_UP).floatValue();
        return config == null ? 0 : config.getRate();
    }
}