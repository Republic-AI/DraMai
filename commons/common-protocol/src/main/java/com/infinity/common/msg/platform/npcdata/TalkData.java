package com.infinity.common.msg.platform.npcdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TalkData {
    //是否在对话中
    public boolean isTalking;
    //对话对象
    public Set<Long> talkingTo;
    //对话内容
    public List<Contents> contents;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Contents{
        //发送者NPCID
        public Long sender;
        //接收者NPCID
        public Long target;
        //123123123
        public Long time;
        //说话内容
        public String content;
    }
}

/*

"talk": [  //对话信息，多人对话
        {
            "isTalking": false, //是否在对话中
            "talkingTo": 123123,//对话对象
            "contents": {  //对话内容
                "sender": "发送者NPCID",
                "time": 123123123,
                "content": "说话内容",
                "target": "接收者NPCID"
            }
        }
    ]

 */