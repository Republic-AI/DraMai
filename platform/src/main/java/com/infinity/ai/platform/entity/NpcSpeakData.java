package com.infinity.ai.platform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class NpcSpeakData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private Long sender;

    private Long receiver;

    private String content;

    private Long gameTime;

    private Long createdAt;

    private int roomId;

    //是否发过twitter
    private Boolean twitter;


    public NpcSpeakData(long sender, long receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }
}
