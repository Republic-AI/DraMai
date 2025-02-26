package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.Goods;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 背包中的全部数据
 */
@Getter
@Setter
public class PlayerBag {

    //身上的全部道具, key=Goods.goodsId
    public Map<Integer, Goods> items = new HashMap<>();
}
