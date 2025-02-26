package com.infinity.ai.service;

import com.infinity.ai.domain.model.PlayerExtend;
import com.infinity.ai.domain.model.TGUser;

import java.util.List;

public interface IPlayerRepository {
    PlayerExtend findById(long id);

    Long findIdByName(String loginName);

    String findNameById(Long playerId);

    PlayerExtend findByUserId(String userId);

    int addPlayerExtend(PlayerExtend playerExtend);

    int delete(String name);

    Integer countSource(Long chatId);

    int addPlayerSource(TGUser user);

    String findSourceByUnitIdType(Long chatId);

    Integer countUnSendTGUser();

    List<TGUser> queryTGUserList(Integer pageIndex, Integer pageSize);

    int updateTGUserState(List<Long> ids);
}



