
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
public class NpcCfg {
    private int id;
    private Integer type;
    private Integer model;
    private String name;
    private Integer hair;
    private Integer top;
    private Integer bottoms;
    private Integer positionX;
    private Integer positionY;

    private String bodyStyle;
    private String dailyPlanReq;
    private String firstName;
    private Integer age;
    private Integer height;
    private Integer weight;
    private String innate;
    private String lastName;
    private String learned;
    private String lifestyle;
    private String livingArea;
    private String property;

    private int map;
}
