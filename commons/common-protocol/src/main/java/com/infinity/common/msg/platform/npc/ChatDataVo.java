package com.infinity.common.msg.platform.npc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDataVo {

    private Long sender;

    private Long receiver;

    private String content;
}
