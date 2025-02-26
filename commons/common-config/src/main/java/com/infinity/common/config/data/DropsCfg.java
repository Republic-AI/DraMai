package com.infinity.common.config.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DropsCfg {
    public int Id;
    public String name;
    public int dropType;
    public int Drop_probability;
    public int Drop_num;
    public List<String> itemWeight;
}//class_end

