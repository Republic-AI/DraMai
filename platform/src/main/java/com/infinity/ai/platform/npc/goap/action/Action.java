package com.infinity.ai.platform.npc.goap.action;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.tables.NpcAction;
import com.infinity.ai.domain.tables.NpcTalk;
import com.infinity.ai.platform.entity.NpcChatData;
import com.infinity.ai.platform.entity.NpcSpeakData;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.repository.NpcChatDataRepository;
import com.infinity.ai.platform.repository.NpcSpeakDataRepository;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.consts.ActionStatus;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.msg.platform.npc.NpcActionBroadRequest;
import com.infinity.common.utils.GsonUtil;
import com.infinity.common.utils.RandomUtils;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.spring.SpringContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.*;

// Action 抽象类表示NPC的行动
@Slf4j
public abstract class Action<T> {
    @Getter
    protected List<Action> preActions; //行动前置行为
    @Getter
    protected Map<String, Boolean> preconditions; // 行动的前置条件
    @Getter
    protected Map<String, Boolean> effects; // 行动的效果
    @Getter
    private int cost; // 行为的成本
    protected ObjectMapper mapper;
    protected int durationTime = 10000;

    @Getter
    @Setter
    protected List<String> oids;

    @Getter
    protected Map<String, Object> params;

    public Action(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        this.preconditions = preconditions;
        this.effects = effects;
        this.cost = cost;
    }

    public Action(Map<Integer, Action> preActions) {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Action(List<Action> preActions) {
        this.preActions = preActions;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Action(List<Action> preActions, List<String> oids) {
        this.preActions = preActions;
        this.oids = oids;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Action(List<Action> preActions, List<String> oids, Map<String, Object> params) {
        this.preActions = preActions;
        this.oids = oids;
        this.params = params;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean checkPreconditions(Map<String, Boolean> state) {
        for (Map.Entry<String, Boolean> precondition : preconditions.entrySet()) {
            if (!state.getOrDefault(precondition.getKey(), false).equals(precondition.getValue())) {
                return false;
            }
        }
        return true;
    }

    public String getOid(Map<String, Object> params) {
        if (oids != null) {
            return oids.get(RandomUtils.randNum(0, oids.size() - 1));
        }
        return null;
    }

    public <T> T convertMapToPOJO(Map<String, Object> map, Class<T> clazz) throws Exception {
        return mapper.convertValue(map, clazz);
    }

    public <T> T convertStringToPOJO(String str, Class<T> clazz) throws Exception {
        return GsonUtil.parseJson(str, clazz);
    }

    public T getParamData(ActionData data) {
        T paramsData;
        Class<T> clz;
        try {
            if (getClass().getGenericSuperclass().equals(CommonAction.class) || getClass().getGenericSuperclass().equals(MoveToAction.class)) {
                clz = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
            } else {
                clz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
            paramsData = convertStringToPOJO(data.getContent(), clz);
        } catch (Exception e) {
            log.error("getParamData error", e);
            throw new BusinessException(ResultCode.FAILURE);
        }
        return paramsData;
    }

    public long execute(NPC npc, ActionData data) {
        if (isPerform(npc)) {
            //log.debug("当前行为正在执行,npcId = {}, actionId={},params={}", npc.getId(), this.getActionType().getCode(), data.getContent());
            //return;
        }

        T paramsData = getParamData(data);

        //是否满足执行行为条件
        if (!canDoing(npc, paramsData)) {
            return 0;
        }

        //行为前处理
        //beforePerform(npc, data);
        //保存当前行为
        long bid = data.getId();
        if (data.getStatus() == ActionStatus.TODO && !performCheck(npc, data, paramsData)) {
            return bid;
        }
        //执行当前行为
        if (data.getStatus() == ActionStatus.TODO) {
            log.debug("当前行为正在执行,npcId = {}, actionId={},params={}", npc.getId(), this.getActionType().getCode(), data.getContent());
            if (data.getStartTime() == null) {
                data.setStartTime(System.currentTimeMillis());
            }
            handleActionSpeak(npc, data, false);
            data.setEndTime(data.getStartTime() + getDurationTime(data));
            firstPerform(npc, data, paramsData);
            data.setStatus(ActionStatus.DOING);
        } else if (data.getStatus() == ActionStatus.DOING) {
            handleActionSpeak(npc, data, true);
            perform(npc, data, paramsData);
        }
        return bid;
    }

    public void handleActionSpeak(NPC npc, ActionData data, boolean random) {
        if (data.getAid() == ActionEnumType.Sleep.getCode()) {
            return;
        }
        if (npc.getNpcModel().get_v().getTalk().isTalking()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (random) {
            if (now - npc.getLastSpeakTime() < GameConsts.ACTION_SPEAK_LIMIT) {
                return;
            }
        }
        //处理说话逻辑
        if (data.getSpeak() != null && data.getSpeak().size() > data.getSpeakIndex()) {
            String speakContent = data.getSpeak().get(data.getSpeakIndex());
            //广播给所有客户端
            sendActionMessage(npc, data, ActionEnumType.Speak.getCode(), "npcId", npc.getId(), "content", speakContent);
            Threads.runAsync(ThreadConst.QUEUE_LOGIC, "Async#saveChat", () -> {
                NpcSpeakData chatData = new NpcSpeakData();
                chatData.setSender(npc.getId());
                chatData.setContent(speakContent);
                chatData.setCreatedAt(now);
                chatData.setGameTime(MapDataManager.getInstance().getGameTime());
                SpringContextHolder.getBean(NpcSpeakDataRepository.class).save(chatData);
            });
            data.setSpeakIndex(data.getSpeakIndex() + 1);
            npc.setLastSpeakTime(now);
        }
    }

    public void beforePerform(NPC npc, ActionData data) {
    }

    public void sendMessage(NPC npc, Object... params) {
        sendMessage(npc, null, null, params);
    }

    public void sendActionMessage(NPC npc, ActionData actionData, int actionType, Object... params) {
        NpcActionBroadRequest request = new NpcActionBroadRequest();
        NpcActionBroadRequest.RequestData data = new NpcActionBroadRequest.RequestData();
        data.setNpcId(npc.getId());
        data.setBid(0);
        data.setActionId(actionType);
        long now = System.currentTimeMillis();
        data.setStartTime(now);
        data.setEndTime(now);
        if (actionData != null) {
            data.setMood(actionData.getMood());
        }
        //data.setUsers(getUserIds(3));

        Map<String, Object> outParams = new HashMap<>();
        if (params != null) {
            int length = params.length;
            if (length % 2 != 0) {
                throw new BusinessException("参数格式有误");
            }

            for (int i = 0; i < length; i += 2) {
                outParams.put((String) params[i], params[i + 1]);
            }
        }

        data.setParams(outParams);
        request.setData(data);
        log.debug("sendMessage,msg={}", request.toString());
        BroadcastMesage.getInstance().send(npc.getId(), null, request.toString());
    }

    public void sendMessage(NPC npc, ActionData actionData, List<Long> playerIds, Object... params) {
        NpcActionBroadRequest request = new NpcActionBroadRequest();
        NpcActionBroadRequest.RequestData data = new NpcActionBroadRequest.RequestData();
        data.setNpcId(npc.getId());
        data.setActionId(this.getActionType().getCode());
        if (actionData != null) {
            data.setStartTime(actionData.getStartTime());
            data.setEndTime(actionData.getEndTime());
        }
        //data.setUsers(getUserIds(3));

        Map<String, Object> outParams = new HashMap<>();
        if (params != null) {
            int length = params.length;
            if (length % 2 != 0) {
                throw new BusinessException("参数格式有误");
            }

            for (int i = 0; i < length; i += 2) {
                outParams.put((String) params[i], params[i + 1]);
            }
        }

        data.setParams(outParams);
        request.setData(data);
        npc.setRequestData(data);
        log.debug("sendMessage,msg={}", request.toString());
        BroadcastMesage.getInstance().send(npc.getId(), playerIds, request.toString());
    }

    private Set<String> getUserIds(int num) {
        Set<String> users = new HashSet<>();
        List<Long> playerIds = GameUserMgr.getRandomKeys(num);
        playerIds.stream().forEach(playerId -> {
            Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
            if (player != null) {
                users.add(player.getPlayerModel().getUserno());
            }
        });
        return users;
    }

    public MapObject findMapObj(String oid) {
        if (!StringUtils.isEmpty(oid)) {
            MapDataManager mapDataManager = MapDataManager.getInstance();
            GameMap gameMap = mapDataManager.getGameMap();
            if (!StringUtils.isEmpty(oid)) {
                MapObject object = gameMap.getObject(oid);
                if (object != null)
                    return object;
            }
        }

        //未找到农田
        //throw new BusinessException(ResultCode.NOT_FOUND_OBJECT_ERROR);
        return null;
    }

    //true：当前行为正在执行
    public boolean isPerform(NPC npc) {
        Map<Long, ActionData> behavior = npc.getNpcModel().get_v().getAction().getBehavior();
        for (Map.Entry<Long, ActionData> entry : behavior.entrySet()) {
            ActionData data = entry.getValue();
            if (data.getStatus() == 0 && data.getAid() == this.getActionType().getCode()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkEnd(NPC npc, ActionData data) {
        long now = System.currentTimeMillis();
        return now - data.getStartTime() > getDurationTime(data);
    }

    public ActionData actionData(NPC npc) {
        NpcAction action = npc.getNpcModel().get_v().getAction();
        ActionData newActionData = newActionData(npc.getId(), 0l);
        action.getBehavior().putIfAbsent(newActionData.getId(), newActionData);
        return newActionData;
    }

    public ActionData newActionData(Long npcId, Long paid) {
        ActionData data = new ActionData();
        data.setId(IDManager.getInstance().getActionId());
        data.setNpcId(npcId);
        data.setAid(this.getActionType().getCode());
        data.setStatus(ActionStatus.TODO);
        data.setPaid(paid == null ? 0L : paid);
        data.setParams(new HashMap<>());
        return data;
    }

    public ActionData saveAction(NPC npc, Map<String, Object> params, long durationTime, List<String> speak, String mood, boolean serverAction) {
        T data;
        Class<T> clz;
        try {
            if (getClass().getGenericSuperclass().equals(CommonAction.class) || getClass().getGenericSuperclass().equals(MoveToAction.class)) {
                clz = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
            } else {
                clz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
            data = convertMapToPOJO(params, clz);
        } catch (Exception e) {
            log.error("getParamData error", e);
            throw new BusinessException(ResultCode.FAILURE);
        }
        ActionData actionData = actionData(npc);
        actionData.setDurationTime(durationTime/ GameConsts.TIME_SPEED);
        actionData.setSpeak(speak);
        actionData.setServerAction(serverAction);
        actionData.setMood(mood);
        String content = content(npc, data);
        if (content != null) {
            actionData.setContent(content);
        }
        return actionData;
    }

    public void addActionLog(NPC npc, ActionData params) {
        addActionLog(params.getId(), npc);
    }

    public void addActionLog(long bid, NPC npc) {
        ActionData actionData = npc.getNpcModel().get_v().getAction().getBehavior().get(bid);
        if (actionData == null) {
            return;
        }
        if (actionData != null) {
            actionData.setEndTime(System.currentTimeMillis());
            actionData.setStatus(1);
            try {
                //RepositoryHelper.addActionLog(actionData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            npc.getNpcModel().get_v().getAction().getBehavior().remove(bid);
        }
    }

    public void afterDoing(NPC npc, ActionData actionData) {
        //处理说话逻辑
        handleActionSpeak(npc, actionData, false);
        this.afterPerform(npc, actionData);
        this.addActionLog(npc, actionData);
        log.debug("当前行为已经结束,npcId = {}, actionId={},params={}", npc.getId(), this.getActionType().getCode(), actionData.getContent());
    }

    public abstract String content(NPC npc, T params);

    public abstract ActionEnumType getActionType();

    public abstract boolean canDoing(NPC npc, T params);

    /**
     * 每次执行
     * @param npc
     * @param actionData
     * @param params
     */
    public void perform(NPC npc, ActionData actionData, T params) {
    }

    /**
     * 首次执行
     * @param npc
     * @param actionData
     * @param params
     */
    public void firstPerform(NPC npc, ActionData actionData, T params) {
    }

    /**
     * 执行检测
     * @param npc
     * @param actionData
     * @param params
     * @return
     */
    public boolean performCheck(NPC npc, ActionData actionData, T params) {
        return true;
    }

    public  void perform(NPC npc, T params) {};

    public void afterPerform(NPC npc, ActionData params) {
        if (this.getActionType() != ActionEnumType.Talk && npc.getNpcModel().get_v().getTalk().isTalking()) {
            return;
        }
        if (!params.isServerAction()) {
            npc.getNpcDataListener().notifyProperty(true);
        }
    }

    public long getDurationTime(ActionData actionData) {
        if (this.getActionType() == ActionEnumType.Buy) {
            return durationTime;
        }
        return actionData.getDurationTime() == 0 ? durationTime : actionData.getDurationTime();
    }
}

