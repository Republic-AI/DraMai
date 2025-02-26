package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.ai.platform.task.gm.GmException;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;

public class OnlineNumCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "online //查询服务器在线人数";
    }


    @Override
    protected boolean exec0(Player owner, String[] params) {
        StringBuffer sb = new StringBuffer();
        sb.append("***************************\n");
        sb.append("当前在线人数: " + PlayerManager.getInstance().getOnlinePlayerMap().size());
        sb.append("\n***************************\n");

        throw new GmException(true, sb.toString());
    }
}
