package com.infinity.ai.login.dto;

import lombok.Data;

@Data
public class TwitterDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Integer expiresIn;

    private String scope;

}
