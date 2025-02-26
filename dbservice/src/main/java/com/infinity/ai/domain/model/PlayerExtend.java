package com.infinity.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerExtend {
    //主键，自增ID
    private Long id;
    //玩家ID
    private String name;
    //用户系统用户ID
    private String userId;
    //用户的邀请码
    private String inviteCode;
    //邀请人ID：
    private String invite;
    //创建时间
    private Long createdate;
}
