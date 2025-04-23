package com.infinity.common.msg.platform.npcdata;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NpcData {
    public Long npcId;
    public String status = "free";

    public InfoData info;
    public List<SellingData> selling;
    public List<ItemsData> items;
    public ActionDataVo action;
    public ActionDataVo curAction;
    public List<MapData> mapData;
    public Surroundings surroundings;
    public TalkData talk;
    public String innverVoice;

    public NpcData(Long npcId) {
        this.npcId = npcId;
        action = new ActionDataVo();
        curAction = new ActionDataVo();
        surroundings = new Surroundings();
        talk = new TalkData();
        items = new ArrayList<>();
        /*selling = new ArrayList<>();
        mapData = new ArrayList<>();
        talk = new ArrayList<>();*/
    }
}
