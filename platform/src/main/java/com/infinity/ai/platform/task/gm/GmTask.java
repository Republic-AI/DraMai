package com.infinity.ai.platform.task.gm;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.common.Pair;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.gm.GmRequest;
import com.infinity.common.msg.platform.gm.GmResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class GmTask extends BaseTask<GmRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.kGmCommand;
    }

    @Override
    protected boolean run0() {
        GmRequest request = this.getMsg();
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            log.debug("player not online,playerId={}", playerId);
            sendMessage(buildResponse(player, request, new Pair<Boolean, String>(false, "player not online")));
            return false;
        }

        var rt = this.exec(player, request.getData().getCmd());
        sendMessage(buildResponse(player, request, rt));
        return true;
    }

    private Pair<Boolean, String> exec(Player owner, String cmd) {
        try {
            String[] params = recursiveReplace(cmd, "  ", " ").split(" ");
            AbstractGmCmd thecmd = GmCmds.get(params[0]);
            if (thecmd == null) {
                return new Pair<Boolean, String>(false, "no such gm cmd, use cmd 'list' to get usage");
            }

            log.info("player[{}] exec gm cmd[{}]", owner.getPlayerID(), cmd);
            return thecmd.exec(owner, params.length == 1 ? new String[0] : Arrays.copyOfRange(params, 1, params.length));
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<Boolean, String>(false, String.valueOf(e.getMessage()));
        }
    }

    private String recursiveReplace(String s, String from, String to) {
        while (s.contains(from)) {
            s = s.replaceAll(from, to);
        }
        return s;
    }

    //查询猫数据
    private BaseMsg buildResponse(Player player, GmRequest msg, Pair<Boolean, String> rt) {
        GmResponse response = new GmResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());
        //response.setCode(rt.first ? 0 : ProtocolCommon.MSG_ERROR);
        response.setCode(0);
        response.setMessage(rt.second);
        return response;
    }
}
