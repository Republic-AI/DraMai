package com.infinity.common.msg.platform.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftData {
    //礼物ID
    private int giftId;
    //礼物名称
    private String name;
    //礼物图标
    private String icon;
    //礼物价格
    private Long price;
    //礼物类型：0：默认
    private int type;
}
