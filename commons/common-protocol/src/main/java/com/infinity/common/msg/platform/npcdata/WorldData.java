package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldData {
    //当前游戏时间
    public Long time;
}
/*

 "world": {
            //当前游戏时间
            "time": "12312312312"
        },

 */