package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.task.gm.GmException;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.common.consts.GoodsSource;

public class AddItemCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "additem 道具ID 道具数量 //添加道具";
    }

    @Override
    protected boolean exec0(Player owner, String[] params) {
        if (params == null || params.length != 2) {
            throw new GmException("params error, item must be not null");
        }

        int itemId = Integer.parseInt(params[0]);
        int num = Integer.parseInt(params[1]);
        owner.getBag().setGoodsValue(itemId, num, true, GoodsSource.GM);
        return true;
    }
}
