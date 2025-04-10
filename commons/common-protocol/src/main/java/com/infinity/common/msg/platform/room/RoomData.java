package com.infinity.common.msg.platform.room;

import lombok.Data;

import java.util.List;

@Data
public class RoomData {

    private int id;

    private List<Long> npcList;

    private int playerCount;
    
    private String bannerUrl;
}
