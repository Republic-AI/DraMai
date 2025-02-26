package com.infinity.ai.platform.manager;

import com.infinity.ai.PPlayer;
import com.infinity.db.db.DBManager;

public class RepositoryUtils {

    public static PlayerTaskDataManager getBean(Long playerId) {
        return PlayerManager.getInstance().getPlayerIgnoreOnline(playerId).getTaskDataManager();
    }

    private static PPlayer getPPlayer(Long playerId) {
        return DBManager.get(PPlayer.class, playerId);
    }
}