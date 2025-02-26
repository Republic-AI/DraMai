package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.task.gm.GmException;
import com.infinity.ai.PPlayer;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.ai.service.IPlayerRepository;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.db.db.DBManager;

public class SetGmCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "gm 账号名 [on=1] //给指定账号设置或取消GM权限 on: 1设置GM，0取消";
    }

    @Override
    protected boolean exec0(Player owner, String[] params) {
        String account = params[0];
        Long id = SpringContextHolder.getBean(IPlayerRepository.class).findIdByName(account);
        if (id == null || id <= 0) {
            throw new GmException(String.format("account[%s] not exist", account));
        }

        int on = 1;
        if (params.length > 1) {
            on = Integer.parseInt(params[1]);
            if (!(on == 0 || on == 1)) {
                throw new GmException("the on value must be 0 or 1");
            }
        }

        PPlayer tarPlayer = DBManager.get(PPlayer.class, id);
        tarPlayer.setGm(on);
        return true;
    }

}
