package com.infinity.ai.platform.dropsystem;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.manager.BagManager;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.utils.Common;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DropAward {
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private int min;
    @Getter
    @Setter
    private int max;
    @Getter
    @Setter
    private int weight;
    @Getter
    @Setter
    private int count;
    @Getter
    @Setter
    private int type;

    private float probability_start_;
    private float probability_end_;

    public Goods build() {
        return BagManager.buildGoods(id, count);
    }

    public DropAward(List<Integer> dropAwardConfig) {
        assert dropAwardConfig.size() == 4;

        this.setId(dropAwardConfig.get(0));
        this.setMin(dropAwardConfig.get(1));
        this.setMax(dropAwardConfig.get(2));
        this.setWeight(dropAwardConfig.get(3));

        if (this.getMin() != this.getMax())
            this.setCount(Common.RandomRangeInt(this.getMin(), this.getMax()));
        else
            this.setCount(this.getMax());

        ItemBaseDataManager dataManager = GameConfigManager.getInstance().getItemBaseDataManager();
        assert dataManager != null;

        ItemCfg itemConfig = dataManager.getItemConfigWithID(this.getId());
        this.setType(0);
        if (itemConfig != null)
            this.setType(itemConfig.getKind());
    }

    public float buildProbability(final float start, final int totalWeight) {
        probability_start_ = start;
        final float fWeight = getWeight();
        final float fTotalWeight = totalWeight;
        final float fProbability = fWeight / fTotalWeight;
        probability_end_ = probability_start_ + fProbability;
        return probability_end_;
    }

    public boolean checkProbability(final float value) {
        return value >= probability_start_ && value < probability_end_;
    }

    @Override
    public String toString() {
        return String.format("drawInfo: id=" + getId() +
                " min=" + getMin() + " max=" + getMax() +
                " weight=" + getWeight() +
                " count=" + getCount() +
                " type=" + getType());
    }
}
