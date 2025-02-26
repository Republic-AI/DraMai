package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionDataVo {
    public String actionName;
    public Integer actionId;
    public Map<String, Object> param;
}

/*
"action": {  //当前正在做的行为
        "actionName": "行为名称",
        "actionId": 100
    },
 */