package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聊天记录对象 chat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 聊天消息ID
     */
    private Long id;

    /**
     * 聊天ID=聊天者ID（小）+聊天者ID（大）+ 类型（暂定：1:私聊 2:群聊，3聊天室 4客服 5系统）
     */
    private String chatId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long targetId;

    /**
     * 消息类型1文字,2图片,3emoji,4音频,5文件
     */
    private Integer msgType;

    /**
     * 消息内容
     */
    private String content;

    //是否弹幕:0:否，1：是
    private Integer barrage;
    //发送则名称'
    private String sname;
    //接受者名称
    private String tname;

    private Long createTime;
    /**
     * 删除时间
     */
    private Long deletedTime;

    /**
     * 删除标志:0:未删除，1:已删除
     */
    private Integer deleted;

    public void generateConversationId(String type) {
        this.chatId = this.senderId > this.targetId ?
                this.senderId + "-" + this.targetId + "-" + type
                : this.targetId + "-" + this.senderId + "-" + type;
    }

    public boolean isSender(long senderId){
        return this.senderId == senderId;
    }
}
