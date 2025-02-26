package com.infinity.ai.platform.dropsystem.Impl;

import com.infinity.ai.platform.dropsystem.DropAward;
import com.infinity.common.config.data.DropsCfg;

import java.util.List;

/**
 * 4.自选礼包：根据配置的道具，自选一个
 * 目前暂时不支持
 */
public class DungeonCustomSelectDropComponent extends AbstractDropComponent {
    public DungeonCustomSelectDropComponent() {
        super();
    }

    @Override
    public void execute() {
        DropsCfg dropsCfg = this.getConfig();
        if (dropsCfg == null)
            return;

        assert dropsCfg != null : "failed to cale the dungeon drops. the drops config is null.";

        /*final int dropType = dropsCfg.getDropType();
        assert dropType == DropFactory.DropType.kFixedDrops.ordinal();*/

        List<List<Integer>> dropItemWeight = formatWeight(dropsCfg.getItemWeight());
        List<DropAward> dropResult = this.getResult();
        // 生成掉落礼包
        dropItemWeight.forEach(dropAward ->
        {
            DropAward aDropAward = new DropAward(dropAward);
            dropResult.add(aDropAward);
        });
    }
}
