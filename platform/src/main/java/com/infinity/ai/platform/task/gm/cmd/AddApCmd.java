package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.ai.platform.task.gm.GmException;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;

public class AddApCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "addap 体力值 //获得体力";
    }

    @Override
    protected boolean exec0(Player owner, String[] params) {
        if (params == null || params.length == 0) {
            throw new GmException("params error, ap must be not null");
        }

        int ap = Integer.parseInt(params[0]);
        owner.getBag().setGoodsValue(GoodsConsts.ITEM_AP_ID, ap, true, GoodsSource.GM);
        return true;
    }
}
