package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.CatCfg;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


public class CatCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, CatCfg> config = new ConcurrentHashMap<>();
    private TreeMap<Double, CatCfg> catTreeMap = new TreeMap();
    private double totalWeight;

    public CatCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        catTreeMap.clear();
        totalWeight = 0;
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        int id = Integer.parseInt(key);
        CatCfg cfg = value.toJavaObject(CatCfg.class);
        config.put(id, cfg);
    }

    public CatCfg get(final int id) {
        return config.get(id);
    }

    public CatCfg randCat() {
        //随机
        /*List<Integer> catIds = new ArrayList(config.keySet());
        Collections.shuffle(catIds);
        return get(catIds.get(0));*/

        //根据猫权重
        ThreadLocalRandom current = ThreadLocalRandom.current();
        double v = current.nextDouble(totalWeight * 0.01);
        SortedMap<Double, CatCfg> subMap = catTreeMap.tailMap(v, true);
        CatCfg catCfg = (subMap.isEmpty()) ? catTreeMap.get(catTreeMap.firstKey()) : subMap.get(subMap.firstKey());
        return catCfg;
    }

    @Override
    protected void afterReload() {
        this.totalWeight = config.values().stream().mapToDouble(CatCfg::getProbability).sum();
        double weight = 0L;

        Iterator<Map.Entry<Integer, CatCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, CatCfg> entry = iter.next();
            CatCfg config = entry.getValue();
            double wt = config.getProbability() / totalWeight;
            wt += weight;
            weight = wt;
            catTreeMap.put(wt, config);
        }
    }

    public Map<Integer, CatCfg> getConfig(){
        return config;
    }
}