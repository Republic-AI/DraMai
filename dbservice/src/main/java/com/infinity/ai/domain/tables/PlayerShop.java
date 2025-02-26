package com.infinity.ai.domain.tables;


import com.infinity.ai.domain.model.Shop;
import com.infinity.ai.domain.model.ShopRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//玩家商店
@Data
public class PlayerShop {

    //商店合并购买记录,k:商店内容表id
    private Map<Integer, Shop> buys = new HashMap<>();

    //购买记录表
    private List<ShopRecord> records = new ArrayList<>();
}
