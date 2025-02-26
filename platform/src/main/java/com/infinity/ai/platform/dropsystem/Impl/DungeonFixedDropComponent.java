package com.infinity.ai.platform.dropsystem.Impl;

import com.infinity.ai.platform.dropsystem.DropAward;
import com.infinity.common.config.data.DropsCfg;

import java.util.List;

/**
 * 2.固定：配多少个道具给多少个道具
 * 目前暂时不支持
 */
public class DungeonFixedDropComponent extends AbstractDropComponent {
    public DungeonFixedDropComponent() {
        super();
    }

    @Override
    public void execute() {
        DropsCfg dropsCfg = this.getConfig();
        if (dropsCfg == null)
            return;

        assert dropsCfg != null : "failed to cale the dungeon drops. the drops config is null.";

        List<List<Integer>> dropItemWeight = formatWeight(dropsCfg.getItemWeight());
        List<DropAward> dropResult = this.getResult();
        // 生成掉落礼包
        dropItemWeight.forEach(dropAward -> {
            DropAward aDropAward = new DropAward(dropAward);
            dropResult.add(aDropAward);
        });
    }
}
