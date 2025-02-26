package com.infinity.common.config.manager;

import com.alibaba.fastjson.JSONObject;
import com.infinity.common.config.data.OpeningCfg;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OpeningCfgManager extends AbstractBaseDataManager {
    private final Map<Integer, OpeningCfg> config = new ConcurrentHashMap<>();
    private Map<String, List<OpeningCfg>> mbtiMap;

    public OpeningCfgManager(final String basePath, final String fileName) {
        super(basePath, fileName);
        reload();
    }

    @Override
    public void decodeConfigObject(final String key, final JSONObject value) {
        int id = Integer.parseInt(key);
        OpeningCfg cfg = value.toJavaObject(OpeningCfg.class);
        config.put(cfg.getId(), cfg);
    }

    public OpeningCfg get(final String parameter) {
        return config.get(parameter);
    }

    @Override
    protected void afterReload() {
        mbtiMap = null;
        mbtiMap = config.values().stream().collect(Collectors.groupingBy(OpeningCfg::getMbti));
    }

    public List<OpeningCfg> getMBTIOpeningList(String mbti) {
        if (mbtiMap == null) {
            return Collections.emptyList();
        }

        return mbtiMap.getOrDefault(mbti, Collections.EMPTY_LIST);
    }

    public String randOpeningCfg(String mbti) {
        List<OpeningCfg> mbtiOpeningList = getMBTIOpeningList(mbti.toUpperCase());
        if (mbtiOpeningList == null || mbtiOpeningList.size() == 0) {
            return "";
        }

        Collections.shuffle(mbtiOpeningList);
        return mbtiOpeningList.get(0).getTags();
    }
}