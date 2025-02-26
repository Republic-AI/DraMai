
package com.infinity.common.config.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class ShopCfg {
    private int id;
    private int storeId;
    private String name;
    private int position;
    public List<Integer> item;
    private String itemDesc;
    private int price;
    private int originalPprice;
    private String discount;
    private String sellersShow;
    private String startTime;
    private String endTime;
    private int restrictedTime;
    private int restrictedFrequency;
    private String icon;
    private int unlock;
    private int unlockNum;

    /**
     * 商店id	商品名称	商品位置	商品	商品描述	使用说明	价格	原价展示	折扣展示	热卖展示	开始时间	结束时间	限购刷新时间	限购次数	图标	解锁条件	解锁参数
     * storeId	name	position	item	itemDesc		price	originalPprice	discount	sellersShow	startTime	endTime	restrictedTime	restrictedFrequency	icon	unlock	unlockNum
     */
}
