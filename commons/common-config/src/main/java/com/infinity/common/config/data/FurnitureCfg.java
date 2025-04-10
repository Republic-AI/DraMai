package com.infinity.common.config.data;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class FurnitureCfg {

    private int id;

    private int npcId;

    private String name;

    private String type;

    private int time;

    private int price;

    private String info;
}
