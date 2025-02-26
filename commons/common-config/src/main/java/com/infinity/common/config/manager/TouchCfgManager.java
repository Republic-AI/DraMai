package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.TouchCfg;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TouchCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, TouchCfg> config = new ConcurrentHashMap<>();
    private TreeMap<Double, TouchCfg> touchTreeMap = new TreeMap();
    private double totalWeight;

    public TouchCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        touchTreeMap.clear();
        totalWeight = 0;
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        TouchCfg cfg = value.toJavaObject(TouchCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public TouchCfg get(final String parameter) {
        return config.get(parameter);
    }

    @Override
    protected void afterReload() {
        this.totalWeight = config.values().stream().filter(d -> d.getType() == 1).mapToDouble(TouchCfg::getWeight).sum();
        double weight = 0L;

        Iterator<Map.Entry<Integer, TouchCfg>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, TouchCfg> entry = iter.next();
            TouchCfg config = entry.getValue();
            if(config.getType() != 1){
                continue;
            }

            double wt = config.getWeight() / totalWeight;
            wt += weight;
            weight = wt;
            touchTreeMap.put(wt, config);
        }

        this.totalWeight = weight;
    }

    public TouchCfg randTouch() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        double v = current.nextDouble(this.totalWeight);
        SortedMap<Double, TouchCfg> subMap = touchTreeMap.tailMap(v, true);
        TouchCfg catCfg = (subMap.isEmpty()) ? touchTreeMap.get(touchTreeMap.firstKey()) : subMap.get(subMap.firstKey());
        return catCfg;
    }

    public String randTouchCfg() {
        List<TouchCfg> collect = config.values().stream().filter(d -> d.getType() == 2).collect(Collectors.toList());
        if (collect == null || collect.size() == 0) {
            return "";
        }

        Collections.shuffle(collect);
        return collect.get(0).getTags();
    }
}