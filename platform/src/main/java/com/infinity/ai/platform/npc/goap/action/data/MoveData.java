package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveData extends BaseData {
    //移动到目标物品
    private String oid;

    private Integer gridX;

    private Integer gridY;

    private Long npcId;
}
