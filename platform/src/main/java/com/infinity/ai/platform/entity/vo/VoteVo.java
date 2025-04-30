package com.infinity.ai.platform.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class VoteVo {

    private Long id;

    private String content;

    private String yesConent;

    private String noContent;

    private int yesCount;

    private int noCount;

    private int roomId;

    //投票状态 0:未开始 1:进行中 2:已结束
    private int state;

    private int animationId;

    private Date startAt;
}
