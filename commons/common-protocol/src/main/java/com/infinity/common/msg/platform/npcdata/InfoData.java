package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//NPC 基础信息
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoData {
    public String name;
    public String firstName;
    public String lastName;
    public String type;
    public int age;
    public int height;
    public int weight;
    public String body_style;
    public String innate;
    public String learned;
    public String lifestyle;
    public String living_area;
    public String property;
}

/*
"info": {
    "name": "NPC名字",
    "first_name": "NPC名字",
    "last_name": "NPC名字",
    "type": "NPC类型",
    "X": 11,
    "Y": 22,
    "age": 20,
    "height": 170,
    "weight": 68,
    "body_style": "body_style",
    "innate": "innate",
    "learned": "learned",
    "lifestyle": "lifestyle",
    "living_area": "living_area",
    "property": "property"
}
 **/