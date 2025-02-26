
package com.infinity.common.config.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class NpcActionCfg {

    private int id;
    private String name;
    private String nameCn;
    private String preconditions;
    private String effects;
}
