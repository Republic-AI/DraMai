package com.infinity.ai.platform.task.npc;

import com.infinity.ai.domain.tables.PlayerNpc;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.NpcStarter;
import com.infinity.ai.platform.npc.data.NpcDataListener;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.ai.PNpc;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.TimerListener;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.config.data.JoinCfg;
import com.infinity.common.config.data.NpcCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.JoinCfgManager;
import com.infinity.common.config.manager.NpcCfgManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.SysParamsConsts;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcOfflineRequest;
import com.infinity.common.msg.platform.player.CharaterSetRequest;
import com.infinity.common.msg.platform.player.CharaterSetResponse;
import com.infinity.common.msg.platform.npc.NpcData;
import com.infinity.common.msg.platform.npc.NpcNotifyRequest;
import com.infinity.common.msg.platform.player.LogoutRequest;
import com.infinity.common.utils.DateUtil;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.TimeUtil;
import com.infinity.db.db.DBManager;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 玩家首次登录NPC角色设置
 */
@Slf4j
public class PlayerNpcSetTask extends BaseTask<CharaterSetRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.CHARATER_SET_COMMAND;
    }

    @Override
    public boolean run0() {
        CharaterSetRequest msg = this.getMsg();
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("CatModifyTask error, playerId params error,playerId={}", playerId);
            return true;
        }

        /*if (NpcManager.getInstance().getOnlineNpcMap().keySet().stream().filter(npcId -> npcId > 100000L).count() >= 6) {
            sendErrorMsg(ErrorCode.CharacterCreationLimitError, ErrorCode.CharacterCreationLimitErrorMessage, msg);
            return true;
        }*/

        //校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return true;
        }

        Long npcId = player.getPlayerModel().get_v().getNpc().getNpcId();

        if (npcId != null && npcId != 0) {
            sendErrorMsg(ErrorCode.PlayerNpcExistError, ErrorCode.PlayerNpcExistErrorMessage, msg);
            return true;
        }

        if (NpcManager.getInstance().getNpcTemplateMap().containsKey(msg.getData().getModel()) && NpcManager.getInstance().getNpcTemplateMap().get(msg.getData().getModel()) > System.currentTimeMillis()) {
            sendErrorMsg(ErrorCode.PlayerNpcExistError, ErrorCode.PlayerNpcExistErrorMessage, msg);
            return true;
        }

        //创建NPC
        PNpc npc = setCharater(player, msg);

        NpcManager.getInstance().getNpcTemplateMap().put(msg.getData().getModel(), npc.getEndTime());

        //返回创建的npc给玩家
        NpcData myNpc = buildNpcData(null, npc);
        sendMessage(buildResponse(myNpc, player, msg));

        //广播创建的npc给其他在线玩家
        //广播给所有的gateway
        broadcastNpc(myNpc, player, msg);

       int lid =  Threads.addListener(ThreadConst.TIMER_1S, npc.getId(),"Npc#remove", new IntervalTimer(npc.getEndTime() - npc.getCreatedate(), 1000) {
            @Override
            public boolean exec0(int interval) {
                NpcManager.getInstance().getNpcTemplateMap().remove(npc.getModel());
                NpcManager.getInstance().removeOnlineNpc(npc.getId());
                //广播npc下线消息给其他玩家
                broadcastOfflineMsg(Set.of(npc.getId()), player, msg);
                player.getPlayerModel().get_v().setNpc(new PlayerNpc());
                DBManager.delete(PNpc.class, npc.getId());
                return true;
            }
        });
       player.setNpcRemoveListenerId(lid);
       return true;
    }

    private void broadcastNpc(NpcData myNpc, Player player, CharaterSetRequest msg) {
        NpcNotifyRequest response = new NpcNotifyRequest();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());
        NpcNotifyRequest.RequestData data = new NpcNotifyRequest.RequestData();
        data.setMyNpc(myNpc);
        response.setData(data);
        BroadcastMesage.getInstance().send(player.getPlayerID(), response.toString());
    }

    private void broadcastOfflineMsg(Set<Long> npcIds, Player player, CharaterSetRequest msg) {
        NpcOfflineRequest response = new NpcOfflineRequest();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        NpcOfflineRequest.RequestData data = new NpcOfflineRequest.RequestData();
        data.setNpcIds(npcIds);
        response.setData(data);
        BroadcastMesage.getInstance().send(player.getPlayerID(), response.toString());
    }

    private PNpc setCharater(Player player, CharaterSetRequest msg) {
        //创建npc
        PNpc npc = null;
        try {
            npc = newDBNpc(msg);
            DBManager.add(npc);
            player.getPlayerModel().get_v().getNpc().setNpcId(npc.getId());
            player.getPlayerModel().get_v().getNpc().getNpcIds().add(npc.getId());
            NpcStarter.getInstance().start(npc, 0);
        } catch (Exception e) {
            if (npc != null && npc.getId() > 0) {
                log.error("new npc[{},{}] fail, err: {}", npc.getId(), npc.getName(), e.getMessage());
                DBManager.delete(PNpc.class, npc.getId());
                player.getPlayerModel().get_v().getNpc().setNpcId(0L);
                player.getPlayerModel().get_v().getNpc().getNpcIds().remove(npc.getId());
                NpcManager.getInstance().removeOnlineNpc(npc.getId());
            }
            throw new IllegalStateException(e);
        }
        return npc;
    }

    public PNpc newDBNpc(CharaterSetRequest msg) {
        PNpc dbData = new PNpc();
        dbData.setId(IDManager.getInstance().getId());
        dbData.setPlayerId(msg.getPlayerId());

        CharaterSetRequest.RequestData charater = msg.getData();
        //角色模型
        if (charater.getModel() > 0)
            dbData.setModel(charater.getModel());
        //角色名称
        if (!StringUtils.isEmpty(charater.getName())) {
            dbData.setName(charater.getName());
        } else {
            dbData.setName("player");
        }
        //职业
        if (!StringUtils.isEmpty(charater.getCareer()))
            dbData.setCareer(charater.getCareer());
        //关键词
        if (!StringUtils.isEmpty(charater.getKeyword()))
            dbData.setKeyword(charater.getKeyword());
        //发型
        if (charater.getHair() > 0)
            dbData.setHair(charater.getHair());
        //top
        if (charater.getTop() > 0)
            dbData.setTop(charater.getTop());
        //bottoms
        if (charater.getBottoms() > 0)
            dbData.setBottoms(charater.getBottoms());

        JoinCfgManager joinCfgManager = GameConfigManager.getInstance().getJoinCfgManager();
        JoinCfg joinCfg = joinCfgManager.get(charater.getModel());

        NpcCfgManager npcCfgManager = GameConfigManager.getInstance().getNpcCfgManager();
        String defaultNpcCfgId = GameConfigManager.getInstance().getSysParamCfgManager()
                .getParameterValue(SysParamsConsts.SYS_DEFAULT_NPC, "10001");
        NpcCfg npcCfg = npcCfgManager.get(Integer.parseInt(defaultNpcCfgId));
        List<NpcCfg> list = npcCfgManager.allNpcCfg();
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        NpcCfg randomElement = list.get(randomIndex);
        dbData.setX(randomElement.getPositionX());
        dbData.setY(randomElement.getPositionY());
        //dbData.setName(npcCfg.getName());
        dbData.setSpeed(0);
        dbData.setCareer("");
        dbData.setKeyword("");
        dbData.setHair(npcCfg.getHair());
        dbData.setTop(npcCfg.getTop());
        dbData.setCreatedate(System.currentTimeMillis());
        dbData.setType(npcCfg.getType());
        dbData.setEndTime(joinCfg.getTime() * DateUtil.OneMinuteMs + dbData.getCreatedate());
        return dbData;
    }

    private BaseMsg buildResponse(NpcData myNpc, Player player, CharaterSetRequest msg) {
        CharaterSetResponse response = new CharaterSetResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());

        CharaterSetResponse.ResponseData data = new CharaterSetResponse.ResponseData();
        data.setMyNpc(myNpc);
        response.setData(data);
        return response;
    }

    public static NpcData buildNpcData(NPC npc, PNpc pNpc) {
        NpcData myNpc = new NpcData();
        //NPC ID
        myNpc.setId(pNpc.getId());
        //NPC名字
        myNpc.setName(pNpc.getName());
        //NPC类型
        myNpc.setType(pNpc.getType());
        //模型ID
        myNpc.setModel(pNpc.getModel());
        //职业
        myNpc.setCareer(pNpc.getCareer());
        //关键词
        myNpc.setKeyword(pNpc.getKeyword());
        //发型
        myNpc.setHair(pNpc.getHair());
        //top
        myNpc.setTop(pNpc.getTop());
        //bottoms
        myNpc.setBottoms(pNpc.getBottoms());
        //NPC移动速度
        myNpc.setSpeed(pNpc.getSpeed());
        //NPC位置X
        myNpc.setX(pNpc.getX());
        //NPC位置Y
        myNpc.setY(pNpc.getY());
        if (npc != null) {
            myNpc.setDressId(npc.getDressId());
            myNpc.setDressEndTime((int) (npc.getDressEndTime() - System.currentTimeMillis()));
        }
        //Npc 过期时间
        myNpc.setEndTime(pNpc.getEndTime() - System.currentTimeMillis());
        GameUser gameUser = GameUserMgr.getGameUser(pNpc.getPlayerId());
        if (gameUser != null) {
            myNpc.setUserNo(gameUser.getUserNo());
        }
        NpcHolder holder = NpcManager.getInstance().getOnlineNpcHolder(pNpc.getId());
        if (holder != null) {
            NpcDataListener npcDataListener = holder.getNpc().getNpcDataListener();
            myNpc.setItems(npcDataListener.getItemsData());
        }
        return myNpc;
    }

}
