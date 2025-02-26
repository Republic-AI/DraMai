package com.infinity.ai.platform.task.player;

import com.infinity.ai.PNpc;
import com.infinity.ai.PPlayer;
import com.infinity.ai.domain.tables.PlayerNpc;
import com.infinity.ai.domain.tables.PlayerTwitter;
import com.infinity.ai.platform.application.Config;
import com.infinity.ai.platform.event.ActionService;
import com.infinity.ai.platform.event.ActionTypeEnum;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NpcStarter;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.ai.service.IPlayerRepository;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.PlayerStatus;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcOfflineRequest;
import com.infinity.common.msg.platform.player.*;
import com.infinity.common.msg.platform.player.LoginRequest.RequestData;
import com.infinity.common.utils.*;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.db.db.DBManager;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.ManagerService;
import com.infinity.task.PlayerRebindDummyTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 用户登录/注册
 */
@Slf4j
public class LoginTask extends BaseTask<LoginRequest> {
    private final IPlayerRepository playerRepository;

    public LoginTask() {
        playerRepository = SpringContextHolder.getBean(IPlayerRepository.class);
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.LOGIN_COMMAND;
    }

    @Override
    public boolean run0() {
        LoginRequest msg = this.getMsg();
        msg.getData().setNickName("0xBuilder");
        long playerId = findPlayerId(msg.getData().getName());
        msg.setPlayerId(playerId);
        boolean isRegister = playerId <= 0;
        PPlayer player = init(isRegister, playerId, msg);

        //校验是否被封号
        if (player.checkStatus(PlayerStatus.CLOSED.getCode())) {
            sendMessage(buildError(ErrorCode.PlayerStatusError, ErrorCode.PlayerStatusErrorMessage, msg));
            return true;
        }

        //添加在线列表
        PlayerManager.getInstance().addOnlinePlayer(player.getId(), player);
        this.refreshGame(player, msg);
        sendMessage(buildResponse(player, msg));

        if (isRegister) {
            /*PlayerManager.getInstance().getOnlinePlayerWithID(player.getId()).bag.addGoods(
                    GoodsConsts.ITEM_MONEY_ID, 30, false, GoodsSource.REGISTER);

            PlayerManager.getInstance().getOnlinePlayerWithID(player.getId()).bag.addGoods(
                    GoodsConsts.ITEM_AP_ID, 100, false, GoodsSource.REGISTER);*/

            //发布注册事件
            ActionService.push(player.getId(), ActionTypeEnum.REGISTER);
        } else {
            //增加玩家npc到在线状态
            Long npcId = player.get_v().getNpc().getNpcId();
            if (npcId != null && npcId > 0) {
                PNpc npc = DBManager.get(PNpc.class, npcId);
                Player playerTemp = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
                NpcStarter.getInstance().start(npc);
                int lid = Threads.addListener(ThreadConst.TIMER_1S, npcId,"Npc#remove", new IntervalTimer(npc.getEndTime() - System.currentTimeMillis(), 1000) {
                    @Override
                    public boolean exec0(int interval) {
                        NpcManager.getInstance().getNpcTemplateMap().remove(npc.getModel());
                        NpcManager.getInstance().removeOnlineNpc(npc.getId());
                        //广播npc下线消息给其他玩家
                        broadcastOfflineMsg(Set.of(npc.getId()), playerTemp, msg);
                        playerTemp.getPlayerModel().get_v().setNpc(new PlayerNpc());
                        DBManager.delete(PNpc.class, npc.getId());
                        return true;
                    }
                });
                playerTemp.setNpcRemoveListenerId(lid);
            }
        }

        //发布登录事件
        ActionService.push(player.getId(), ActionTypeEnum.LOGIN);

        //登录重置每日刷新数据
        PlayerRebindDummyTask task = new PlayerRebindDummyTask("LoginTask#run", player.getId(), () -> {
            Player p = PlayerManager.getInstance().getOnlinePlayerWithID(player.getId());
            p.dailyReset(false);
        });
        ManagerService.getTaskManager().add(task);
        return true;
    }

    private BaseMsg buildResponse(PPlayer player, BaseMsg msg) {
        final long playerId = player.getId();
        LoginResponse response = new LoginResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(playerId);

        LoginResponse.ResponseData data = new LoginResponse.ResponseData();
        Long nowTime = System.currentTimeMillis();
        data.setTimestamp(nowTime);
        PlayerData info = new PlayerData();
        info.setPlayerId(player.getUserno());
        //info.setCharaterValue(player.get_v().getNpc().getNpcId());
        info.setTime(MapDataManager.getInstance().getGameTime());
        info.setRoomId(player.get_v().getLive().getRoomId());
        data.setPlayer(info);
        TwitterData twitterData = new TwitterData();
        PlayerTwitter playerTwitter = player.get_v().getPlayerTwitter();
        twitterData.setTwitterUserName(playerTwitter.getTwitterUserName());
        twitterData.setTwitterScreenName(playerTwitter.getTwitterScreenName());
        data.setTwitterData(twitterData);

        final int duration = 300; // 以秒为单位
        final long clientNodeId = msg.getPlayerId();
        // 可逆的token参数
        String paramToken = String.format("%d|%d|%d|%d", playerId, nowTime, duration, clientNodeId);

        try {
            final byte[] encryptToken = DesUtils.encrypt(paramToken.getBytes("utf-8"), Common.getInstance().getTokenKey());
            final String strToken = ConvertUtil.getHexStr(encryptToken, false);
            data.setToken(strToken);
        } catch (Exception e) {
            log.error("failed to encrypt the token. msg={}, playerID={}, nowTime={}, duration={}, clientNodeId={}, tokenKey={}",
                    e.getMessage(), playerId, nowTime, duration, clientNodeId, ConvertUtil.getHexStr(Common.getInstance().getTokenKey(), false));
            e.printStackTrace();
        }
        data.setLoginType(player.get_v().getBase().getLoginType());
        response.setData(data);
        return response;
    }

    public void refreshGame(PPlayer player, LoginRequest msg) {
        //查找可用service,随机分配room节点和game节点
        GameUser gu = new GameUser();
        gu.setPlatformServiceId(Config.getInstance().getNodeId());
        gu.setUserId(player.getId());
        gu.setUserNo(player.getUserno());
        gu.setNickName(player.getNickname());
        gu.setGatewayServiceId(msg.getGateway());
        gu.setSessionId(msg.getSessionId());
        GameUserMgr.addGameUser(gu, null);
    }

    private PPlayer init(boolean isRegister, long playerId, LoginRequest msg) {
        //存在就更新；不存在就新增
        if (isRegister) {
            return newPPlayer(msg);
        }

        RequestData requestData = msg.getData();
        PPlayer player = DBManager.get(PPlayer.class, playerId);
        player.setNickname(requestData.getNickName());
        player.get_v().getBase().setZone(requestData.getTimeZone());
        player.get_v().getBase().setOs(0);
        player.get_v().getBase().setAvatar(requestData.getAvatar());
        player.get_v().getBase().setLoginType(requestData.getLoginType());
        player.setLasttime(System.currentTimeMillis());
        player.setLoginip("1");
        return player;
    }

    private long findPlayerId(String name) {
        Long playerId = playerRepository.findIdByName(name);
        return playerId == null ? 0 : playerId;
    }

    private PPlayer newPPlayer(LoginRequest msg) {
        PPlayer player = null;
        RequestData requestData = msg.getData();
        try {
            player = new PPlayer();
            long now = System.currentTimeMillis();
            player.setName(requestData.getName());
            player.setCreatedate(now);
            player.setNickname(requestData.getNickName());
            player.setUserno(IDGenerator.genId());
            player.setStatus(PlayerStatus.RUNNED.getCode());
            player.setLasttime(now);
            player.setPwd(getPwd(requestData.getName(), requestData.getPassword()));
            player.get_v().getBase().setSex(requestData.getSex());
            player.get_v().getBase().setAvatar(requestData.getAvatar());
            player.get_v().getBase().setLoginType(requestData.getLoginType());
            player.setLoginip("1");
            DBManager.add(player);
            //addPlayerExtend(player);
        } catch (Exception e) {
            if (player != null && player.getId() > 0) {
                log.error("new player[{},{}] fail, err: {}", player.getId(), player.getName(), e.getMessage());
                DBManager.delete(PPlayer.class, player.getId());
                //playerRepository.delete(player.getName());
                PlayerManager.getInstance().removeOnlinePlayer(player.getId());
            }
            throw new IllegalStateException(e);
        }
        return player;
    }

    private String getPwd(String loginName, String pwd) {
        try {
            return MD5Utils.md5hex32(loginName.concat(pwd));
        } catch (Exception e) {
            log.error("loginTask getPwd error, {}", e.getMessage());
        }
        return "";
    }

    private void broadcastOfflineMsg(Set<Long> npcIds, Player player, LoginRequest msg) {
        NpcOfflineRequest response = new NpcOfflineRequest();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        NpcOfflineRequest.RequestData data = new NpcOfflineRequest.RequestData();
        data.setNpcIds(npcIds);
        response.setData(data);
        BroadcastMesage.getInstance().send(player.getPlayerID(), response.toString());
    }
}
