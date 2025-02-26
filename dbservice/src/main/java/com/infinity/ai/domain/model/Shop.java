package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品购买对象 shop
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商店内容表id：shop.xls.id
     */
    private Integer shopId;

    /**
     * 商店ID
     */
    private Integer storeId;

    /**
     * 购买次数
     */
    private Integer buyCount;

    /**
     * 最后购买时间
     */
    private Long lastTime;
}
