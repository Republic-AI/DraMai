package com.infinity.ai.platform.task.gm.cmd;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.task.gm.AbstractGmCmd;
import com.infinity.ai.platform.task.gm.GmCmds;
import com.infinity.ai.platform.task.gm.GmException;

public class ListCmd extends AbstractGmCmd {

    @Override
    public String usage() {
        return "list //列出所有GM指令";
    }

    @Override
    protected boolean exec0(Player owner, String[] params) {
        StringBuffer sb = new StringBuffer();
        sb.append("***************************\n");
        sb.append("*在聊天框以'//'开头输入指定命令,带参数的用空格分隔\n");
        sb.append("*现支持的所有GM命令如下所示，后面为命令说明，中括号表示该参数可选\n");
        sb.append("***************************\n");

        for (var e : GmCmds.getAll().entrySet()) {
            sb.append(e.getValue().usage()).append("\n");
        }

        throw new GmException(true, sb.toString());
    }
}
