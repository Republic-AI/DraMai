package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品购买记录对象 shop_record
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商店内容表id：shop.xls.id
     */
    private Integer shopId;

    /**
     * 商城id
     */
    private Integer storeId;

    /**
     * 支付金额
     */
    private Long amount;

    /**
     * 购买数量
     */
    private Integer count;

    //购买时间
    private Long createTime;

}
