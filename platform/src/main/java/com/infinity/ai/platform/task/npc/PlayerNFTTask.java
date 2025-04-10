package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.constant.NFTConstant;
import com.infinity.ai.platform.manager.NFTManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NFTData;
import com.infinity.common.msg.platform.npc.PlayerNFTRequest;
import com.infinity.common.msg.platform.npc.PlayerNFTResponse;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


/**
 * 玩家操作NFT
 * from:H5客户端
 * target:platform
 */
@Slf4j
public class PlayerNFTTask extends BaseTask<PlayerNFTRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.PLAYER_NFT_COMMAND;
    }

    @Override
    public boolean run0() {
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("SignTask error, playerId params error,playerId={}", playerId);
            return false;
        }

        //todo 校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return false;
        }
        GameUser gameUser = GameUserMgr.getGameUser(playerId);
        if (gameUser == null) {
            //用户不在线
            return false;
        }
        PlayerNFTRequest msg = this.getMsg();
        int id = msg.getData().getId();
        int op = msg.getData().getOp();
        Map<Integer, NFTData> nftInfo = NFTManager.getInstance().getNftMap();
        NFTData nftData = nftInfo.get(id);
        int state = nftData.getState();
        if (op == NFTConstant.OPEN_NFT) {
            if (state != NFTConstant.INIT) {
                return false;
            }
            nftData.setState(NFTConstant.OPEN);
            nftData.setUserNo(gameUser.getUserNo());
            Threads.addListener(ThreadConst.TIMER_1S, id,"NFT#reset", new IntervalTimer(180000, 0) {
                @Override
                public boolean exec0(int interval) {
                    NFTData nftData1 = NFTManager.getInstance().getNftMap().get(id);
                    if (nftData1.getState() == NFTConstant.INIT) {
                        return true;
                    }
                    List<String> mintList = NFTManager.getInstance().getNFTList();
                    String mint = mintList.isEmpty() ? "" : mintList.get(0);
                    NFTManager.getInstance().getNftMap().put(id, new NFTData(id, NFTConstant.INIT, "", mint, null, 0));
                    PlayerNFTResponse response = new PlayerNFTResponse();
                    response.setPlayerId(playerId);
                    PlayerNFTResponse.ResponseData data = new PlayerNFTResponse.ResponseData();
                    response.setData(data);
                    data.setNftInfo(NFTManager.getInstance().getNftMap().get(id));
                    BroadcastMesage.getInstance().send(playerId, response.toString());
                    return true;
                }
            });
        } else if (op == NFTConstant.PICK_NFT) {
            if (state != NFTConstant.OPEN) {
                return false;
            }
            /*if (!NFTManager.getInstance().reciveNFT(nftData, msg.getData().getAddress())) {
                sendErrorMsg(ErrorCode.NftTransferFailedError, ErrorCode.NftTransferFailedErrorMessage, msg);
                return false;
            }*/
            nftData.setState(NFTConstant.CLOSE);
            nftData.setUserNo(gameUser.getUserNo());
        }
        PlayerNFTResponse response = new PlayerNFTResponse();
        response.setPlayerId(playerId);
        PlayerNFTResponse.ResponseData data = new PlayerNFTResponse.ResponseData();
        response.setData(data);
        data.setNftInfo(NFTManager.getInstance().getNftMap().get(id));
        BroadcastMesage.getInstance().send(playerId, response.toString());
        return false;
    }


}
