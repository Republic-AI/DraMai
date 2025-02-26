package com.infinity.ai.service;

import com.infinity.ai.domain.model.Chat;

import java.util.List;

public interface IChatRepository {

    //新增
    int add(Chat chat);

    List<Chat> queryPageList(Integer pageIndex, Integer pageSize, Chat chat);

    Integer count(Chat chat);
}



