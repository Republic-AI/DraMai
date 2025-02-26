
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
public class NpcTypeCfg {
    private int id;
    private String name;
    private String nameCn;
    private List<Long> actions;

}
