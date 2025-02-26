package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 行为日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionData implements Serializable {
    private static final long serialVersionUID = 1L;
    //ID
    private Long id;
    //npcID
    private Long npcId;
    //当前行为ID
    private Integer aid;
    //前一个行为ID：对应ID
    private Long paid;
    //创建时间
    private Long createTime;
    //行为开始时间
    private Long startTime;
    //行为结束时间
    private Long endTime;
    //行为内容
    private String content;
    //行为状态:0：正在做，1：完成 2：准备做开没开始 3 异常结束
    private int status;
    //其他参数：
    private Map<String, Object> params;
    //持续时间
    private long durationTime;
    //说话内容
    private List<String> speak;
    //是否是服务器动作
    private boolean serverAction;
    //移动次数
    private int moveCount;
    //讲话
    private int speakIndex;
    //心情
    private String mood;
}
