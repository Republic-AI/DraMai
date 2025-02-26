package com.infinity.common.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum GoodsSource {
    //未知
    UNKNOWN(-1, "未知"),
    //注册
    REGISTER(100, "注册"),
    //商店充值
    RECHARGE(101, "充值"),
    //每日签到
    SIGN(102, "每日签到"),
    //三日签到
    THREE_SIGN(103, "三日签到"),
    //系统邮件
    SYS_EMAIL(104, "邮件"),
    //祈祷、抽卡
    DRAW_CARD(105, "祈祷"),
    //任务/成就
    TASK_ACHIEVEMENT(106, "任务/成就"),
    //PVP
    PVP(108, "PVP"),
    //背包
    BAG(109, "背包"),
    //
    CHAT(110, "聊天"),
    //
    RESET(111, "每日重置"),
    //分享
    SHARE(112, "分享"),
    //GM 命令
    GM(113, "GM"),
    //商店购买
    SHOP(114, "商店"),
    //触摸
    TOUCH(115, "触摸"),
    //逗猫
    TEASE(116, "逗猫"),
    //打工
    WORK(116, "打工"),

    //种植
    FARMING(117,"种植"),
    SALE(118,"销售"),
    BUY(119,"购买"),
    COOK(120,"做饭"),
    Feeding(121,"投喂"),
    Slaughter(122,"屠宰"),
    TYPE_TO_EARN(123,"type"),
    NPC_INIT(124, "NPC初始化");

    private final int code;
    private final String name;

    public static GoodsSource getByCode(int code) {
        for (GoodsSource value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
