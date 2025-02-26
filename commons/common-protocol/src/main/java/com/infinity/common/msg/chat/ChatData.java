package com.infinity.common.msg.chat;

import com.infinity.common.utils.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatData {
    //消息id
    private Long msgId;
    //发送者姓名
    private String sname;
    //发送者
    private String sender;
    //接收者
    private Long target;
    //消息类型
    private Integer type;
    //消息内容
    private String content;
    //聊天时间
    private Long time;
    //是否弹幕:0：是，1：否
    public int barrage;
    //接收者Id
    public String receiver;
    //接收者名字
    public String rName;
    //回复的消息ID
    public Long replyMsgId;
    //playerId
    public Long targetPlayerId;

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
    
}
