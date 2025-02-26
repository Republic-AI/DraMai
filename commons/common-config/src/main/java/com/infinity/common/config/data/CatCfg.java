package com.infinity.common.config.data;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class CatCfg {

    private int id;//猫ID
    private String name;//猫的名字
    private String breed;//猫品种
    private String mbti;//MBTI
    private List<String> cat_characters;//性格特征
    private String avatar;//猫的头像
    private float cv;//初始猫德
    private float probability;//概率
    private float cv_factor;//加权猫德
    private float earningRate;//赚钱速率
    private String tags;//文案

/*
猫ID	猫的名字	猫品种	MBTI	猫的头像	概率	加权猫德	初始猫德	文案
id	name	breed	mbti	avatar	probability	cv_factor	cv	tags
 */
}
