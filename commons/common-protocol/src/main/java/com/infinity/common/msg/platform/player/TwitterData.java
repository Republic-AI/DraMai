package com.infinity.common.msg.platform.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TwitterData {

    private String expiresIn;

    private String twitterUserName;

    private String twitterScreenName;
}
