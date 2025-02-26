
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
public class EarningCVCfg {
    private int id;
    private int iqEnd;
    private int iqStart;
    private int level;
    private float rate;

}
