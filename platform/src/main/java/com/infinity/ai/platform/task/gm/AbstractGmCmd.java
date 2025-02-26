package com.infinity.ai.platform.task.gm;

import com.infinity.ai.platform.manager.Player;
import com.infinity.common.Pair;

public abstract class AbstractGmCmd {

    protected boolean perm(Player owner) {
        if (owner.getPlayerModel().getGm() != 1)
            return false;

        String addr = "127.0.0.1";//owner.getPlayerModel().get_v().getBase().getLastLoginAddr();
        return (addr != null && (addr.startsWith("192.168.") || addr.startsWith("127.0.")));
    }

    final public Pair<Boolean, String> exec(final Player owner, final String[] params) {
        try {
            if (!perm(owner)) {
                return new Pair<Boolean, String>(false, "permission denied");
            }

            boolean success = this.exec0(owner, params);
            return new Pair<Boolean, String>(success, success ?
                    String.format("execute '%s' success\n", this.name())
                    : String.format("execute '%s' fail, usage:\n%s", this.name(), usage()));
        } catch (GmException e) {
            return new Pair<Boolean, String>(e.isSuccess(),
                    String.format("execute '%s' %s, details:\n%s", this.name(), e.isSuccess() ? "success" : "fail", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<Boolean, String>(false, String.format("execute '%s' fail, details:\n%s", this.name(), String.valueOf(e.getMessage())));
        }
    }

    final public String name() {
        return this.usage().split(" ")[0];
    }

    public abstract String usage();

    protected abstract boolean exec0(final Player owner, final String[] params);
}
