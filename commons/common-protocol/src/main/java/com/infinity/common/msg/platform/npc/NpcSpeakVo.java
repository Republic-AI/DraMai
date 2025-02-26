package com.infinity.common.msg.platform.npc;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class NpcSpeakVo {

    private Long sender;

    private Long receiver;

    private String content;

    private Long gameTime;

    private Long createdAt;


    public NpcSpeakVo(Long sender, Long receiver, String content, Long gameTime, Long createdAt) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.gameTime = gameTime;
        this.createdAt = createdAt;
    }
}
