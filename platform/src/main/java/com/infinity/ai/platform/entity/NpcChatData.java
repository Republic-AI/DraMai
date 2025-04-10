package com.infinity.ai.platform.entity;

import com.infinity.common.msg.platform.npc.ChatDataVo;
import com.infinity.db.db.JsonConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class NpcChatData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    @Convert(converter = JsonConverter.class)
    @Column(name = "speak_data_list", columnDefinition = "TEXT")
    private List<ChatDataVo> speakDataList = new ArrayList<>();

    private Long gameTime;

    private Long createdAt;

    private int roomId;

    //是否发过twitter
    private Boolean twitter;

}
