package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class Surroundings {
    //public List<Location> locations;
    public List<People> people;
    public List<Item> items;

    public Surroundings() {
        //locations = new ArrayList<>();
        people = new ArrayList<>();
        items = new ArrayList<>();
    }

    public void clear() {
        people.clear();
        items.clear();
    }

    public void addItem(Item item) {
        if (items != null && item != null) {
            items.add(item);
        }
    }

    public void addPeople(People people) {
        if (this.people != null && people != null) {
            this.people.add(people);
        }
    }

    //周边的地理位置信息
    @Data
    @AllArgsConstructor
    @Builder
    public static class Location {
        public String name;
        public String address;
    }

    //周边人
    @Data
    @Builder
    public static class People {
        //NPC id
        public Long npcId;
        //状态
        public String status;
    }

    //周边物品信息
    @Data
    @Builder
    public static class Item {
        public String oid;
        public String objName;
        public String type;
        /*public Integer X;
        public Integer Y;
        public String status;
        public Set<String> availableActions;*/
    }
}

/*

"surroundings": { //周边信息
        "locations": [ //周边的地理位置信息
            {
                "name": "杂货店",
                "address": "坐标地址"
            }
        ],
        "people": [ //周边人
            {
                "npcId": 10002,
                "status": "状态"
            }
        ],
        "items": {  //周边物品信息
            "objName": "地图物品名称",
            "oid": "物品ID",
            "type": "物品类型",
            "X": 11, //物品坐标X
            "Y": 11, //物品坐标Y
            "status": "燃烧",
            "availableActions": [ //可用动作
                "关火",
                "开火"
            ]
        }
    }

 */