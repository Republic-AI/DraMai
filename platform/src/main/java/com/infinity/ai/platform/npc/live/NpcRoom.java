package com.infinity.ai.platform.npc.live;

import com.infinity.ai.domain.entity.MapItemData;
import com.infinity.ai.domain.tables.VMap;
import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.entity.vote.UserVoteData;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.event.ActionTypeEnum;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.map.MapUtil;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.ai.platform.repository.vote.UserVoteDataRepository;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.config.data.DramaCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.msg.platform.room.RoomItemChangeResponse;
import com.infinity.common.msg.platform.room.RoomItemData;
import com.infinity.common.msg.platform.vote.NotifyVoteResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.network.MessageSender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Slf4j
public class NpcRoom {

    private MapManager mapManager = new MapManager();

    private int roomId;

    private GameMap gameMap;

    private List<Long> npcList = new ArrayList<>();

    private Set<Long> playerList = new HashSet<>();

    //type -> FurnitureData
    private Map<String, FurnitureData> furnitureMap = new ConcurrentHashMap<>();

    private volatile Date voteStart;

    private volatile int state;

    private volatile int section;

    public int playerCount() {
        return playerList.size();
    }

    public NpcRoom(int roomId) {
        this.roomId = roomId;
        Optional<VoteData> voteDataOptional =  SpringContextHolder.getBean(VoteDataRepository.class).findTopByRoomIdOrderByVoteIdDesc(roomId).stream().findFirst();
        VoteData voteData = voteDataOptional.orElse(null);
        this.voteStart = voteData == null ? new Date() : voteData.getStartAt();
        this.state = voteData == null ? VoteConstant.VOTE_STATE_NORMAL : voteData.getState();
        NpcRoom npcRoom = this;
        Threads.addListener(ThreadConst.TIMER_1S, roomId,"Vote#check", new IntervalTimer(10000, 1000){
            @Override
            public boolean exec0(int interval) {
                long now = System.currentTimeMillis();
                boolean change = false;
                VoteData voteData1 = null;
                if (npcRoom.getState() == VoteConstant.VOTE_STATE_NORMAL && now >= npcRoom.getVoteStart().getTime() + VoteConstant.DURATION_TIME) {
                    List<VoteData> voteDataList =  SpringContextHolder.getBean(VoteDataRepository.class).findByRoomIdAndState(roomId, VoteConstant.VOTE_STATE_NORMAL);
                    voteData1 = !voteDataList.isEmpty() ? voteDataList.get(0) : null;
                    if (voteData1 == null) {
                        return false;
                    }
                    voteData1.setState(VoteConstant.VOTE_STATE_END);
                    npcRoom.setState(VoteConstant.VOTE_STATE_END);
                    SpringContextHolder.getBean(VoteDataRepository.class).save(voteData1);
                    change = true;
                    if (voteData1.getAnimationId() != 0 && voteData1.getYesCount() >= voteData1.getNoCount()) {
                        startDrama(voteData1.getAnimationId(), npcRoom);
                    }
                }
                if (npcRoom.getState() == VoteConstant.VOTE_STATE_END && now >= npcRoom.getVoteStart().getTime() + VoteConstant.DURATION_TIME + VoteConstant.CD_TIME) {
                    List<VoteData> voteDataList =  SpringContextHolder.getBean(VoteDataRepository.class).findByRoomIdAndState(roomId, VoteConstant.VOTE_STATE_END);
                    voteData1 = !voteDataList.isEmpty() ? voteDataList.get(0) : null;
                    if (voteData1 == null) {
                        return false;
                    }
                    voteData1 = copyVoteData(voteData1);
                    npcRoom.setVoteStart(new Date());
                    npcRoom.setState(VoteConstant.VOTE_STATE_NORMAL);
                    SpringContextHolder.getBean(VoteDataRepository.class).save(voteData1);
                    change = true;
                }
                if (change) {
                    for (Long playerId : playerList) {
                        NotifyVoteResponse notifyVoteResponse = buildResponse(voteData1);
                        notifyVoteResponse.setPlayerId(playerId);
                        GameUser gameUser = GameUserMgr.getGameUser(playerId);
                        log.debug("send msg -> {},{}", gameUser, notifyVoteResponse);
                        if (gameUser != null) {
                            MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), notifyVoteResponse);
                        }
                    }
                }
                return false;
            }
        });
        Threads.addListener(ThreadConst.TIMER_1S, roomId,"RoomItem#check", new IntervalTimer(10000, 10000){
            @Override
            public boolean exec0(int interval) {
                VMap vMap = MapDataManager.getInstance().getMap().get_v();
                List<MapItemData> list = vMap.getMapItemData().computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());
                for (MapItemData mapItemData : new ArrayList<>(list)) {
                    if (mapItemData.getEndTime() < System.currentTimeMillis()) {
                        list.remove(mapItemData);
                        RoomItemChangeResponse response = new RoomItemChangeResponse();
                        RoomItemChangeResponse.ResponseData responseData = new RoomItemChangeResponse.ResponseData();
                        RoomItemData roomItemData = new RoomItemData();
                        roomItemData.setId(mapItemData.getId());
                        responseData.setRoomItemData(roomItemData);
                        response.setData(responseData);
                        for (long playerId : npcRoom.getPlayerList()) {
                            response.setPlayerId(playerId);
                            GameUser gameUser = GameUserMgr.getGameUser(playerId);
                            log.debug("send msg -> {},{}", gameUser, response);
                            if (gameUser != null) {
                                MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), response);
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    public void startDrama(int animationId, NpcRoom npcRoom) {
        npcRoom.setState(VoteConstant.VOTE_STATE_DRAMA);
        Map<Integer, List<DramaCfg>> map = GameConfigManager.getInstance().getDramaCfgManager().getAnimationCfg(animationId);
        final int[] section = {0};
        Threads.addListener(ThreadConst.TIMER_1S, roomId,"Drama#tick", new IntervalTimer(1000, 1000){
            @Override
            public boolean exec0(int interval) {
                if (section[0] != 0) {
                    //检测是否到下一个动作
                    boolean sectionEnd = true;
                    for (long npcId : npcList) {
                        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
                        NPC npc = npcHolder.getNpc();
                        if (npc.getCurrent() != 0 || !npc.getTodo().isEmpty()) {
                            sectionEnd = false;
                        }
                    }
                    if (!sectionEnd) {
                        return false;
                    }
                } else {
                    for (long npcId : npcList) {
                        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
                        NPC npc = npcHolder.getNpc();
                        npc.clear();
                    }
                }
                section[0]++;
                List<DramaCfg> dramaCfgList = map.get(section[0]);
                if (dramaCfgList == null) {
                    npcRoom.setState(VoteConstant.VOTE_STATE_END);
                    return true;
                }
                for (long npcId : npcList) {
                    NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
                    NPC npc = npcHolder.getNpc();
                    for (DramaCfg dramaCfg : dramaCfgList) {
                        if (dramaCfg.getNpcId() == npc.getId()) {
                            npc.doAction(dramaCfg.getAction(), convertParam(dramaCfg.getAction(), dramaCfg.getParam()), true);
                        }
                    }
                }
                return false;
            }
        });
    }

    public Map<String, Object> convertParam(int actionId, String param) {
        Map<String, Object> params = new HashMap<>();
        if (actionId == ActionEnumType.Move.getCode()) {
            String[] strings = param.split(",");
            params.put("gridX", Integer.parseInt(strings[0])/32);
            params.put("gridY", Integer.parseInt(strings[1])/32);
        } else if (actionId == ActionEnumType.Speak.getCode()) {
            params.put("content", param);
        } else if (actionId == ActionEnumType.Share.getCode()) {
            String[] strings = param.split(",");
            params.put("npcId", strings[2]);
            params.put("content", strings[0]);
            params.put("itemId", strings[1]);
        } else if (actionId == ActionEnumType.PlaceItem.getCode()) {
            String[] strings = param.split(",");
            params.put("gridX", Integer.parseInt(strings[1])/32);
            params.put("gridY", Integer.parseInt(strings[2])/32);
            params.put("itemId", Integer.parseInt(strings[0]));
            params.put("time", Integer.parseInt(strings[3]));
        }
        return params;
    }

    public NotifyVoteResponse buildResponse(VoteData voteData) {
        NotifyVoteResponse notifyVoteResponse = new NotifyVoteResponse();
        NotifyVoteResponse.ResponseData responseData = new NotifyVoteResponse.ResponseData();
        notifyVoteResponse.setData(responseData);
        responseData.setRoomId(voteData.getRoomId());
        responseData.setState(voteData.getState());
        responseData.setEndTime(voteData.getStartAt().getTime() + VoteConstant.DURATION_TIME - System.currentTimeMillis());
        responseData.setContent(voteData.getContent());
        responseData.setYesContent(voteData.getYesConent());
        responseData.setNoContent(voteData.getNoContent());
        return notifyVoteResponse;
    }

    public VoteData copyVoteData(VoteData voteData) {
        VoteData ret = new VoteData();
        ret.setRoomId(voteData.getRoomId());
        ret.setState(VoteConstant.VOTE_STATE_NORMAL);
        ret.setStartAt(new Date());
        ret.setContent(voteData.getContent());
        ret.setYesConent(voteData.getYesConent());
        ret.setNoContent(voteData.getNoContent());
        ret.setAnimationId(voteData.getAnimationId());
        return ret;
    }
}
