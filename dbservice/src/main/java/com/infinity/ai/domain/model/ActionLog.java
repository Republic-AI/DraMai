package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 行为日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionLog implements Serializable {
    private static final long serialVersionUID = 1L;
    //ID
    private Long id;
    //npcID
    private Long npcId;
    //当前行为ID
    private Integer aid;
    //前一个行为ID：对应ID
    private Long paid;
    //行为开始时间
    private Long startTime;
    //行为结束时间
    private Long endTime;
    //行为内容
    private String content;
    //创建时间
    private Long createTime;
}
