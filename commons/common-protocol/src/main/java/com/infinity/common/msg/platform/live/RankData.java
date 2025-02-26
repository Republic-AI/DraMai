package com.infinity.common.msg.platform.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankData {
    //玩家名称
    private String name;
    //积分
    private long score;
    //排名
    private int sort;
}
