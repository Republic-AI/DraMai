
package com.infinity.common.config.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class TeaseCatCfg {
    private int id;
    private int itemId;
    private String name;
    private List<String> useEffect;
}
