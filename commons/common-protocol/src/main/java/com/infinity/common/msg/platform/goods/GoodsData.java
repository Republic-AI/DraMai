package com.infinity.common.msg.platform.goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 玩家拥有的道具对象 goods
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsData {
    private static final long serialVersionUID = 1L;

    /**
     * 玩家ID
     */
    private Long playerId;

    /**
     * 道具ID
     */
    private Integer goodsId;

    /**
     * 道具类型,如猫币、猫粮、小鱼干等
     */
    private Integer goodsType;

    /**
     * 道具数量
     */
    private Integer count;

    /**
     * 道具上限
     */
    private Integer maxLimit;
}
