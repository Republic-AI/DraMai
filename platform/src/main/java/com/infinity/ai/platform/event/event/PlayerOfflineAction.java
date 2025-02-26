package com.infinity.ai.platform.event.event;

import com.infinity.ai.platform.event.ActionParams;
import lombok.extern.slf4j.Slf4j;

/***
 * 玩家离线
 */
@Slf4j
public class PlayerOfflineAction extends AbstractAction {

    /***
     * taskPrams 格式: [1]
     * @param params
     */
    @Override
    public void execute(ActionParams params) {
        log.debug("玩家离线了,playerId={}", params.getPlayerId());
    }
}
