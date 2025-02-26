package com.infinity.common.msg.platform.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerData {
    //玩家ID
    private String playerId;
    //是否已经设置角色:0:否，1：是
    private int charater;
    //当前游戏时间
    private Long time;
    //roomId房间号，目前就是NPCID，等于0说明是第一次
    private Long roomId;

    public void setCharaterValue(Long npcId){
        this.charater = (npcId == null)? 0: npcId.intValue();
    }
}
