package com.infinity.ai.platform.task.player;

import com.infinity.ai.domain.tables.PlayerTwitter;
import com.infinity.ai.platform.entity.dto.TwitterDto;
import com.infinity.ai.platform.entity.dto.TwitterUserDto;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.TwitterManager;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.TwitterCodeRequest;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 绑定twitter
 */
@Slf4j
public class TwitterCodeTask extends BaseTask<TwitterCodeRequest> {

    private TwitterManager twitterManager;

    public TwitterCodeTask() {
        twitterManager = SpringContextHolder.getBean(TwitterManager.class);
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.TWITTER_CODE_COMMAND;
    }

    @Override
    public boolean run0() {
        TwitterCodeRequest msg = this.getMsg();
        log.debug("twitter code, msg = {}", msg.toString());
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(msg.getPlayerId());
        if (player == null) {
            return false;
        }
        TwitterCodeRequest.RequestData data = msg.getData();
        TwitterDto twitterDto = twitterManager.requestBearerToken(data.getCode(), data.getRefreshToken(), data.getState());
        if (twitterDto == null) {
            throw new BusinessException("get twitterDto error!");
        }
        TwitterUserDto twitterUserDto = twitterManager.getUserInfoByToken(twitterDto.getAccessToken());
        PlayerTwitter playerTwitter = player.getPlayerModel().get_v().getPlayerTwitter();
        saveUserTwitterBind(data.getCode(), data.getState(), playerTwitter, twitterDto, twitterUserDto);
        return true;
    }

    private void saveUserTwitterBind(String code, String state, PlayerTwitter playerTwitter, TwitterDto twitterDto, TwitterUserDto twitterUserDto) {
        playerTwitter.setCode(code);
        playerTwitter.setState(state);
        playerTwitter.setAccessToken(twitterDto.getAccessToken());
        playerTwitter.setRefreshToken(twitterDto.getRefreshToken());
        playerTwitter.setTokenType(twitterDto.getTokenType());
        playerTwitter.setScope(twitterDto.getScope());
        playerTwitter.setExpiresIn(String.valueOf(twitterDto.getExpiresIn()));
        playerTwitter.setTwitterUserId(twitterUserDto.getTwitterUserId());
        playerTwitter.setTwitterUserName(twitterUserDto.getTwitterUserName());
        playerTwitter.setTwitterScreenName(twitterUserDto.getTwitterScreenName());
    }
}
