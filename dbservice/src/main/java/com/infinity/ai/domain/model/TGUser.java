package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TGUser {
    //主键，自增ID
    private Long id;
    //TG ID
    private Long chatId;
    //TG 名称
    private String name;
    //来源渠道
    private String source;
    //消息状态：0=未发送,1=已发送
    private int state;
    //创建时间
    private Long createdate;
    //更新时间
    private Long updateTime;
    //删除标志:0:未删除，1:已删除
    private Integer deleted = 0;
}
