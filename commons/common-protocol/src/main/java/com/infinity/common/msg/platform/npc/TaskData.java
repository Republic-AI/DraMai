package com.infinity.common.msg.platform.npc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//玩家任务数据
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskData implements Serializable {
    // 玩家ID
    private Long playerId;
    // 任务ID#
    private Integer taskId;
    // 状态：0:初始化,1:任务进行中,2:任务完成,3:领取完成#
    private Integer status;
    //条件值1，已完成的值#
    private Integer value1;
    // 任务是否已解锁:0:未解锁,1:已解锁
    private Integer unlock;
}
