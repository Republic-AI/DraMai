package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.CatIQCfg;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CatIQCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, CatIQCfg> config = new ConcurrentHashMap<>();
    private TreeMap<Double, CatIQCfg> treeMap = new TreeMap();
    private double totalWeight;

    public CatIQCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        treeMap.clear();
        totalWeight = 0;
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        CatIQCfg cfg = value.toJavaObject(CatIQCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public CatIQCfg get(final String parameter) {
        return config.get(parameter);
    }

    @Override
    protected void afterReload() {
        this.totalWeight = config.values().stream().mapToDouble(CatIQCfg::getRate).sum();
        double weight = 0L;

        Iterator<Map.Entry<Integer, CatIQCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, CatIQCfg> entry = iter.next();
            CatIQCfg config = entry.getValue();

            double wt = config.getRate() / totalWeight;
            wt += weight;
            weight = wt;
            treeMap.put(wt, config);
        }

        this.totalWeight = weight;
    }

    public CatIQCfg randConfig() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        double v = current.nextDouble(this.totalWeight);
        SortedMap<Double, CatIQCfg> subMap = treeMap.tailMap(v, true);
        CatIQCfg catCfg = (subMap.isEmpty()) ? treeMap.get(treeMap.firstKey()) : subMap.get(subMap.firstKey());
        return catCfg;
    }

    public int getCatIQ() {
        CatIQCfg catIQCfg = randConfig();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(catIQCfg.getIqStart(), catIQCfg.getIqEnd());
    }
}