package com.infinity.ai.login.controller;

import com.infinity.ai.login.dto.TwitterDto;
import com.infinity.ai.login.dto.TwitterUserDto;
import com.infinity.ai.login.model.UserTwitterBind;
import com.infinity.ai.login.service.TwitterService;
import com.infinity.ai.login.service.UserService;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;


@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    @Autowired
    private TwitterService twitterService;

    @Autowired
    private UserService userService;


    @GetMapping("/getTwitterCode")
    public Result<TwitterDto> getTwitterCode(String code, String state, @SessionAttribute String userId) {

        log.info("getTwitterCode userId: {}, code: {}, state: {}", userId, code, state);
        TwitterDto twitterDto = twitterService.requestBearerToken(code, null, state);
        if (twitterDto == null) {
            throw new BusinessException("get twitterDto error!");
        }

        TwitterUserDto twitterUserDto = twitterService.getUserInfoByToken(twitterDto.getAccessToken());
        // 查询是否已经有用户绑定了twitter
        UserTwitterBind twitterBind = userService.queryUserTwitterBindByTwitterUserId(twitterUserDto.getTwitterUserId());
        if (twitterBind != null) {
            saveUserTwitterBind(code, state, twitterBind, userId, twitterDto, twitterUserDto);
            throw new BusinessException("twitter is already bind the other user!");
        }

        // 落到数据库进行绑定
        UserTwitterBind userTwitterBind = userService.queryUserTwitterBind(userId);
        if (userTwitterBind == null) {
            userTwitterBind = new UserTwitterBind();
        }
        saveUserTwitterBind(code, state, userTwitterBind, userId, twitterDto, twitterUserDto);
        return Result.ok();
    }

    private void saveUserTwitterBind(String code, String state, UserTwitterBind userTwitterBind, String userId, TwitterDto twitterDto, TwitterUserDto twitterUserDto) {
        /*userTwitterBind.setCode(code);
        userTwitterBind.setUserId(userId);
        userTwitterBind.setState(state);
        userTwitterBind.setAccessToken(twitterDto.getAccessToken());
        userTwitterBind.setRefreshToken(twitterDto.getRefreshToken());
        userTwitterBind.setTokenType(twitterDto.getTokenType());
        userTwitterBind.setScope(twitterDto.getScope());
        userTwitterBind.setExpiresIn(String.valueOf(twitterDto.getExpiresIn()));
        userTwitterBind.setTwitterUserId(twitterUserDto.getTwitterUserId());
        userTwitterBind.setTwitterUserName(twitterUserDto.getTwitterUserName());
        userTwitterBind.setTwitterScreenName(twitterUserDto.getTwitterScreenName());
        userService.saveUserTwitterBind(userTwitterBind);*/
    }
}
