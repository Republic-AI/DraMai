package com.infinity.ai.platform.dropsystem.Impl;

import com.infinity.ai.platform.dropsystem.DropAward;
import com.infinity.ai.platform.dropsystem.IDropComponent;
import com.infinity.common.config.data.DropsCfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDropComponent implements IDropComponent {
    private DropsCfg config;
    private final List<DropAward> dropAwards = new LinkedList<>();

    protected AbstractDropComponent() {
    }

    protected DropsCfg getConfig() {
        return this.config;
    }

    public void init(DropsCfg dropsCfg) {
        this.config = dropsCfg;
    }

    public void dispose() {
    }

    public abstract void execute();

    public List<DropAward> getResult() {
        return dropAwards;
    }

    public List<List<Integer>> formatWeight(List<String> weightList) {
        if (weightList == null || weightList.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<List<Integer>> resultList = new ArrayList<>();
        for (String weightListStr : weightList) {
            List<Integer> numList = new ArrayList<>();
            String[] split = weightListStr.split("\\|");
            for (String num : split) {
                numList.add(Integer.parseInt(num));
            }
            resultList.add(numList);
        }

        return resultList;
    }
}
