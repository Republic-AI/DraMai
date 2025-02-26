package com.infinity.ai.platform.dropsystem;


import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.platform.dropsystem.Impl.DungeonCustomSelectDropComponent;
import com.infinity.ai.platform.dropsystem.Impl.DungeonFixedDropComponent;
import com.infinity.ai.platform.dropsystem.Impl.DungeonWeightDropComponent;
import com.infinity.common.config.data.DropsCfg;
import com.infinity.common.config.manager.GameConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 1.副本掉落：触发后,根据配置的权重随机给一个道具
 * 2.固定：配多少个道具给多少个道具
 * 3.随机礼包：根据配置的权重随机给一个道具
 * 4.自选礼包：根据配置的道具，自选一个
 */
@Slf4j
public class DropFactory {
    public enum DropType {
        // 0. 未知的掉落类型
        kNoneDrops,
        // 1.副本掉落：触发后,根据配置的权重随机给一个道具
        kDungeonWeightDrops,
        // 2.固定：配多少个道具给多少个道具
        kFixedDrops,
        // 3.随机礼包：根据配置的权重随机给一个道具
        kRandomDrops,
        // 4.自选礼包：根据配置的道具，自选一个
        kCustomSelectDrops
    }

    private static final class DropFactoryHolder {
        private static final DropFactory kInstance = new DropFactory();
    }

    public static DropFactory getInstance() {
        return DropFactoryHolder.kInstance;
    }

    /**
     * 根据  dropId 返回 对应的物品,如果dropId不存在 返回null
     *
     * @param dropId
     * @return
     */
    public static List<Goods> getGoodsByDrops(int dropId) {
        DropsCfg cfg = GameConfigManager.getInstance().getDropBaseData().getDropsConfigWithID(dropId);
        if (cfg == null) {
            log.error("Drops tab,dropId not found");
            return Collections.EMPTY_LIST;
        }

        IDropComponent component = getInstance().create(cfg.dropType);
        if (component == null) {
            log.error("failed to get drop component. dropIndex={}, dropType={}", dropId, cfg.dropType);
            return Collections.EMPTY_LIST;
        }

        List<Goods> items = new ArrayList<>();
        component.init(cfg);
        component.execute();
        List<DropAward> result = component.getResult();
        for (DropAward dropAward : result) {
            Goods build = dropAward.build();
            items.add(build);
        }
        component.dispose();
        return items;

    }

    public final IDropComponent create(final int dropType) {
        DropType eType = DropType.values()[dropType];
        IDropComponent dropComponent = null;

        switch (eType) {
            case kRandomDrops:
            case kDungeonWeightDrops:
                dropComponent = new DungeonWeightDropComponent();
                break;

            case kFixedDrops:
                dropComponent = new DungeonFixedDropComponent();
                break;

            case kCustomSelectDrops:
                dropComponent = new DungeonCustomSelectDropComponent();
                break;

            default:
            case kNoneDrops:
                break;
        }

        return dropComponent;
    }
}

