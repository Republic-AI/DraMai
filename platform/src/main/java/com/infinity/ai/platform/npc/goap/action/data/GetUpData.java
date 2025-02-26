package com.infinity.ai.platform.npc.goap.action.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUpData extends BaseData {
    //睡在哪个床上：床的名称
    private String oid;
}
