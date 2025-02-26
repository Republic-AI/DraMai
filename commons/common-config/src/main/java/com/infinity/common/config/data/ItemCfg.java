package com.infinity.common.config.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemCfg {
    public int Id;
    public String Name;
    public int Kind;
    public int Subtype;
    public String icon;
    public List<Integer> DropType;
    public int bag;
}//class_end

