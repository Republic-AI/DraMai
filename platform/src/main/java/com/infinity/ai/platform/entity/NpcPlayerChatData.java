package com.infinity.ai.platform.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class NpcPlayerChatData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private long npcId;

    private long playerId;

    private String content;

    private boolean npcSend;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
