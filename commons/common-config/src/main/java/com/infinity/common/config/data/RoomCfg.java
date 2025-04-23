package com.infinity.common.config.data;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class RoomCfg {
    private int id;
    private String name;
    private String map;
    private String banner;
    private int show;
    private int order;
}
