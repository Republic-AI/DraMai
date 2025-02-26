package com.infinity.ai.login.service;

import com.infinity.ai.login.model.User;
import com.infinity.ai.login.model.UserTwitterBind;
import com.infinity.ai.login.repository.UserRepository;
import com.infinity.ai.login.repository.UserTwitterBindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTwitterBindRepository userTwitterBindRepository;

    public User queryUser(String userId) {
        return userRepository.queryUser(userId);
    }

    public void saveUserTwitterBind(UserTwitterBind userTwitterBind) {
        userTwitterBindRepository.save(userTwitterBind);
    }

    public UserTwitterBind queryUserTwitterBindByTwitterUserId(String twitterUserId) {
        return userTwitterBindRepository.queryUserTwitterBindByTwitterUserId(twitterUserId);
    }

    public UserTwitterBind queryUserTwitterBind(String userId) {
        return userTwitterBindRepository.queryUserTwitterBind(userId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
