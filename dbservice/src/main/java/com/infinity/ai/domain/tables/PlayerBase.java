package com.infinity.ai.domain.tables;

import lombok.Data;

//基本信息
@Data
public class PlayerBase {
    //玩家头像
    private int avatar;
    //性别 0:未知，1:男,2女
    private Integer sex;
    //时区
    private int zone;
    //语言
    private int language;
    //设备操作系统：0:pc,1:安卓,2:苹果
    private Integer os;
    //登录类型：0:游客模式,1:邮箱
    private Integer loginType;
}
