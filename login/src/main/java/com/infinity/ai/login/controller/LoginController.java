package com.infinity.ai.login.controller;

import com.infinity.ai.login.model.User;
import com.infinity.ai.login.service.TwitterService;
import com.infinity.ai.login.service.UserService;
import com.infinity.common.msg.platform.login.UserLoginRequest;
import com.infinity.common.msg.platform.login.UserLoginResponse;
import com.infinity.common.msg.platform.player.LoginRequest;
import com.infinity.common.msg.platform.player.LoginResponse;
import com.infinity.common.utils.IDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/common")
@Slf4j
public class LoginController {

    @Autowired
    private TwitterService twitterService;
    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public UserLoginResponse twitterLogin(@RequestBody UserLoginRequest loginRequest, HttpSession session) {
        String userId = loginRequest.getData().getUserId();
        User user = userService.queryUser(userId);
        if (user == null) {
            user = new User();
            user.setUserId(IDGenerator.genId());
            userService.saveUser(user);
        }
        UserLoginResponse loginResponse = new UserLoginResponse();
        UserLoginResponse.ResponseData data = new UserLoginResponse.ResponseData();
        loginResponse.setData(data);
        data.setUserId(user.getUserId());
        data.setStatus(user.getUserStatus());
        session.setAttribute("userId", user.getUserId());
        return loginResponse;
    }
}
