package com.infinity.ai.domain.tables;

import com.infinity.ai.domain.model.Goods;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * twitter数据
 */
@Data
public class PlayerTwitter {
    private String code;

    private String state;

    private String accessToken;

    private String refreshToken;

    private String twitterUserId;

    private String tokenType;

    private String scope;

    private String expiresIn;

    private String twitterUserName;

    private String twitterScreenName;
}
