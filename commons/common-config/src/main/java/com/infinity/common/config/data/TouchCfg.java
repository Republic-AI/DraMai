
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
public class TouchCfg {
    private Integer id;
    private String tags;
    private int type;
    private float weight;
}
