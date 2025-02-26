package com.infinity.common.config.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TaskCfg {
    public int Id;
    public String taskName;
    public int Position;
    public int minPosition;
    public int refurbish;
    public int taskType;
    public int Value1;
    public List<String> Value2;
    public List<String> Value3;
    public int taskReward;
    public int Unlock;
    public int Unlockvalue;
}//class_end