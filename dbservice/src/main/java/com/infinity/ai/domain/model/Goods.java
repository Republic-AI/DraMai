package com.infinity.ai.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家拥有的道具对象 goods
 */
@Data
public class Goods implements Serializable {

    /**
     * 道具ID
     */
    public Integer goodsId;

    /**
     * 玩家ID
     */
    public Long playerId;

    /**
     * 道具类型,如猫币、猫粮、小鱼干等
     */
    public Integer goodsType;

    /**
     * 道具数量
     */
    public Integer count;

    /**
     * 道具上限
     */
    public Integer maxLimit;

}
