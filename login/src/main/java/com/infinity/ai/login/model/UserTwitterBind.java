package com.infinity.ai.login.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user_twitter_bind")
public class UserTwitterBind {

    @Id
    private Long id;

    private String code;

    private String userId;

    private String state;

    private String accessToken;

    private String refreshToken;

    private String twitterUserId;
}
