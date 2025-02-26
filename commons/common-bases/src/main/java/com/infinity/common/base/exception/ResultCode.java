package com.infinity.common.base.exception;


import lombok.Getter;

/**
 * 返回结果代码
 */
@Getter
public enum ResultCode {
    //业务处理成功
    SUCCESS(0, "业务处理成功"),
    //业务处理失败
    FAILURE(1, "业务处理失败"),


    //NPCID格式错误
    NPCID_FORMAT_ERROR(10000, "npcId format error, must be greater than 0"),
    //NPCID 不存在
    NPC_NOT_EXIST_ERROR(10001, "npc does not exist"),
    //目标对象未找到
    NOT_FOUND_OBJECT_ERROR(10002, "target object not found"),
    //聊天参数格式错误
    CHAT_FORMAT_ERROR(10003, "chat context is null"),
    //礼物配置不存在
    GIFT_CONFIG_ERROR(10004, "the gift does not exist"),
    //金币不足
    COINS_NOT_ENOUGH_ERROR(10005, "not enough coins"),
    ;

    public final int code;
    public final String message;

    ResultCode(int code, String name) {
        this.code = code;
        this.message = name;
    }


}