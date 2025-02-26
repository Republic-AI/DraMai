package com.infinity.common.msg.platform.npc;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NpcChatVo {

    private List<ChatDataVo> speakDataList = new ArrayList<>();

    private Long gameTime;

    private Long createdAt;

    public NpcChatVo(List<ChatDataVo> speakDataList, Long gameTime, Long createdAt) {
        this.speakDataList = speakDataList;
        this.gameTime = gameTime;
        this.createdAt = createdAt;
    }
}
