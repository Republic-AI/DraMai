package com.infinity.common.config.data;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class SysParamCfg {
    private int id;
    private String parameter;
    private String value;
    private String remark;
}
