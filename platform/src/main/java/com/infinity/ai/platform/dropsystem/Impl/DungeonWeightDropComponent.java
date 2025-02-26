package com.infinity.ai.platform.dropsystem.Impl;


import com.infinity.ai.platform.dropsystem.DropAward;
import com.infinity.ai.platform.dropsystem.DropFactory;
import com.infinity.common.config.data.DropsCfg;
import com.infinity.common.utils.Common;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 1.副本掉落：触发后,根据配置的权重随机给一个道具
 * 配置格式：道具id，数量min，数量max，权重值|道具id，数量min，数量max，权重值|道具id，数量min，数量max，权重值……
 * 计算方式为：某个道具的ID的权重 / 所有权重的总和 = 该道具出现的概率
 */
@Slf4j
public class DungeonWeightDropComponent extends AbstractDropComponent {
    public DungeonWeightDropComponent() {
        super();
    }

    @Override
    public void execute() {
        DropsCfg dropsCfg = this.getConfig();
        if (dropsCfg == null)
            return;

        assert dropsCfg != null : "failed to cale the dungeon drops. the drops config is null.";

        final int dropType = dropsCfg.getDropType();
        assert dropType == DropFactory.DropType.kDungeonWeightDrops.ordinal();

        // 计算掉的概率
        int dropProbability = dropsCfg.getDrop_probability();
        List<List<Integer>> dropItemWeight = formatWeight(dropsCfg.getItemWeight());
        int dropCount = dropsCfg.getDrop_num();

        for (int iIndex = 0; iIndex < dropCount; ++iIndex) {
            // 如果不掉包，那么直接返回
            if (!this.checkDrop(dropProbability))
                return;

            List<DropAward> dropAwards = new LinkedList<>();
            AtomicInteger countWeight = new AtomicInteger();

            // 生成掉落礼包
            dropItemWeight.forEach(dropAward ->
            {
                DropAward aDropAward = new DropAward(dropAward);
                dropAwards.add(aDropAward);
                countWeight.addAndGet(aDropAward.getWeight());
            });

            // 计算概率
            AtomicReference<Float> sumProbability = new AtomicReference<>((float) 0);
            dropAwards.forEach(dropAward -> {
                sumProbability.set(dropAward.buildProbability(sumProbability.get(), countWeight.get()));
            });

            // 生成掉落数据概率
            final float randomDropItem = ThreadLocalRandom.current().nextFloat();

            List<DropAward> dropResult = this.getResult();
            dropAwards.forEach(dropAward -> {
                if (dropAward.checkProbability(randomDropItem)) {
                    dropResult.add(dropAward);
                }
            });
        }
    }

    private boolean checkDrop(final int dropProbability) {
        final int min = 0;
        final int max = 10000;
        final int value = Common.RandomRangeInt(min, max);
        log.debug("checkDrop, the value={}, probability={}", value, dropProbability);
        return value >= 0 && value <= dropProbability;
    }
}
