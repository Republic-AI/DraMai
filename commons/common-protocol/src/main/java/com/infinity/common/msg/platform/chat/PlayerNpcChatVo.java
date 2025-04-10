package com.infinity.common.msg.platform.chat;

import lombok.Data;

@Data
public class PlayerNpcChatVo {

    private String content;

    private long time;

    private boolean npcSend;
}
