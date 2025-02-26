
package com.infinity.common.config.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class NpcItemCfg {

    private int id;
    private Integer item;
    private String name;
    private List<Integer> npcType;
    private Integer npcTypeId;
    private Integer type;
}
