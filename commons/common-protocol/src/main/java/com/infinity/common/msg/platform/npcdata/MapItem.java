package com.infinity.common.msg.platform.npcdata;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

//周边物品信息
@Data
@Builder
public class MapItem {
    public String oid;
    public String objName;
    public String type;
    public Integer X;
    public Integer Y;
    public String status;
    public Set<String> availableActions;
}