package com.infinity.ai.platform.npc;

import com.infinity.ai.PNpc;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.tables.NpcTalk;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.map.GameMap;
import com.infinity.ai.platform.map.MapUtil;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.map.object.MapElement;
import com.infinity.ai.platform.map.object.MapElementType;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.character.CharacterType;
import com.infinity.ai.platform.npc.data.NpcDataListener;
import com.infinity.ai.platform.npc.event.Event;
import com.infinity.ai.platform.npc.event.EventListener;
import com.infinity.ai.platform.npc.goap.Goal;
import com.infinity.ai.platform.npc.goap.action.Action;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.npc.live.Room;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.consts.ActionStatus;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.msg.platform.command.NpcCommandSyncResponse;
import com.infinity.common.msg.platform.npc.NpcActionBroadRequest;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.platform.npc.NpcActionResponse;
import com.infinity.common.utils.GsonUtil;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

@Getter
@Setter
@Slf4j
public abstract class NPC extends MapElement implements EventListener {
    //NPC ID
    public Long id;
    public String name;
    //队伍ID
    private int teamId = 0;
    //true 停止运行，false：运行
    private volatile boolean exit = false;
    private volatile boolean init = false;
    //在线NPC持有者
    private NpcHolder holder;
    //NPC数据监听器
    private final NpcDataListener npcDataListener;

    //当前位置
    private Position position;
    //事件集合
    private List<Event> events;

    private List<Goal> goals; // NPC的目标列表
    private Map<String, Boolean> worldState; // NPC的世界状态
    private List<Action> currentPlan; // 当前的行动计划
    private Goal currentGoal; // 当前的目标

    private Map<Integer, Action> actions; // NPC的行动列表
    protected List<Long> todo;//要做的
    protected volatile long current;//正在做的
    protected volatile long talkCurrent;//当前正在说话
    protected volatile int curActionType;
    protected volatile String curOid;
    private Set<Long> playerIds;

    private long lastReplyTime;
    private long lastActiontTime;
    private long lastSpeakTime;

    private List<Long> replyMsgIds = new ArrayList<>();
    //需要执行的OID列表索引
    protected int index;
    //需要执行的OID列表
    protected List<String> oidList = new ArrayList<>();

    private volatile NpcActionBroadRequest.RequestData requestData;//当前行为推送的消息

    private int roomId;

    private int dressId;

    private long dressEndTime;

    private LinkedBlockingDeque<String> commands = new LinkedBlockingDeque<>();

    public NPC(Long id, String name) {
        this.id = id;
        this.name = name;
        this.events = new ArrayList<>();
        this.actions = new HashMap<>();
        this.currentPlan = new ArrayList<>();
        this.todo = new CopyOnWriteArrayList<>();
        this.registAction();
        this.npcDataListener = new NpcDataListener(this);
        this.playerIds = new HashSet<>();
    }

    //初始化数据
    public void initialize() {
        this.init();
        this.initPosition();
        //注册事件
        //NpcManager.getInstance().getEventManager().subscribe(this);
        //初始化数据
        if (npcType() != CharacterType.Player) {
            this.npcDataListener.initNpcData();
        }

        Room.getInstance().init(this.id);
    }


    //启动npc
    public void start() {
        //玩家NPC由玩家操控
        /*if (npcType() == CharacterType.Player) {
            return;
        }*/

        //启动每帧服务
        Threads.addListener(ThreadConst.TIMER_50MS, 0,"npc#run", new IntervalTimer(5000, 50) {
            @Override
            public boolean exec0(int interval) {
                return run();
            }
        });
        //启动innervoice服务
        Threads.addListener(ThreadConst.TIMER_1S, 0,"npc#innerVoice", new IntervalTimer(5000, 10000) {
            @Override
            public boolean exec0(int interval) {
                return innerVoice();
            }
        });
    }

    public boolean innerVoice() {
        String command = fetchCommand();
        if (StringUtils.isNotBlank(command)) {
            NpcCommandSyncResponse response = new NpcCommandSyncResponse();
            NpcCommandSyncResponse.RequestData requestData = new NpcCommandSyncResponse.RequestData();
            response.setData(requestData);
            requestData.setNpcId(getId());
            requestData.setCommand(command);
            response.setPlayerId(null);
            response.setGateway(null);
            response.setSessionId(null);
            response.setRequestId(RequestIDManager.getInstance().RequestID(false));
            log.debug("sendInnerVoiceMsg===================npcId = {},:{}", getId(), response.toString());
            MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPythonService, response);
        }
        return false;
    }

    public void addCommand(String command) {
        if (StringUtils.isNotBlank(command)) {
            commands.offerLast(command);
        }
    }

    public String fetchCommand() {
        return commands.pollFirst();
    }

    //按固定帧数执行NPC业务
    public boolean run() {
        if (exit) {
            RoomManager.getInstance().getMapManager(this.getRoomId()).removeElement(this);
            log.debug("npc has exit,npcId={},name={}", id, name);
            return true;
        }
        if (!init) {
            RoomManager.getInstance().getMapManager(this.getRoomId()).addElement(this);
            log.debug("npc init,npcId={},name={}", id, name);
            init = true;
        }

        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回true 则停止运行，false：持续运行
        return false;
    }

    public void update() {
        //log.debug("npcId={},name={}", id, name);
        //检测AI并初始化
        long now = System.currentTimeMillis();
        if (now - lastActiontTime > 3000) {
            //initServerAi();
        }
        if (getId() < GameConsts.MAX_NPC_ID) {
            //处理聊天回复
            handleReplyChat();
            //处理talk
            handleTalk();
        }
        //处理行为
        handleAction();

        //事件变更
        events.forEach(event -> {
            if (event != null) {
                /*switch (event.getType()) {
                    case EventType.TIME_CHANGE:
                        //handlePlayerApproach((PlayerApproachEvent) event);
                        break;
                    case "TimeChange":
                        //handleTimeChange((TimeChangeEvent) event);
                        break;
                }*/
            }
        });

        events.clear();
    }

    private void handleTalk() {
        try {
            ActionData curActionData = null;
            if (talkCurrent == 0) {
                for (long bid : todo) {
                    curActionData = getNpcModel().get_v().getAction().getBehavior().get(bid);
                    if (curActionData.getAid() == ActionEnumType.Talk.getCode()) {
                        talkCurrent = bid;
                        break;
                    }
                    curActionData = null;
                    talkCurrent = 0;
                }
                if (talkCurrent != 0) {
                    todo.remove(talkCurrent);
                }
            } else {
                curActionData = getNpcModel().get_v().getAction().getBehavior().get(talkCurrent);
            }
            if (curActionData != null) {
                Map<String, Object> params = GsonUtil.toMap(curActionData.getContent());
                if (!checkPosition(params, this.getRoomId())) {
                    ActionData toActionData = todo.isEmpty() ? null : getNpcModel().get_v().getAction().getBehavior().get(todo.get(0));
                    if (curActionType != ActionEnumType.Move.getCode() && (toActionData == null || toActionData.getAid() != ActionEnumType.Move.getCode())) {
                        doAction(ActionEnumType.Move.getCode(), params, true);
                    }
                    return;
                }
                Action action = actions.get(curActionData.getAid());
                action.execute(this, curActionData);
                finishAction(curActionData);
                talkCurrent = 0;
            } else {
                talkCurrent = 0;
            }
        } catch (Exception e) {
            log.error("talkAction执行异常", e);
            todo.remove(talkCurrent);
            talkCurrent = 0;
        }
    }

    public void handleAction() {
        try {
            Action action = null;
            ActionData curActionData = null;
            if (current == 0) {
                if (!todo.isEmpty()) {
                    lastActiontTime = System.currentTimeMillis();
                    for (long bid : todo) {
                        curActionData = getNpcModel().get_v().getAction().getBehavior().get(bid);
                        if (curActionData.getAid() == ActionEnumType.Talk.getCode()) {
                            curActionData = null;
                            continue;
                        }
                        current = bid;
                        break;
                    }
                    if (curActionData == null) {
                        return;
                    }
                    if (curActionData.getMoveCount() > 5) {
                        getNpcModel().get_v().getAction().getBehavior().remove(current);
                        todo.remove(current);
                        current = 0;
                        return;
                    }
                    curActionType = curActionData.getAid();
                    action = actions.get(curActionData.getAid());
                    Map<String, Object> params = GsonUtil.toMap(curActionData.getContent());
                    if (action.getActionType() != ActionEnumType.ReplyChat && action.getActionType() != ActionEnumType.Move && action.getActionType() != ActionEnumType.GotoNpc && !checkPosition(params, this.getRoomId())) {
                        action.beforePerform(this, curActionData);
                        doAction(ActionEnumType.Move.getCode(), params, curActionData.isServerAction());
                        curActionData.setMoveCount(curActionData.getMoveCount() + 1);
                        current = 0;
                        curActionType = 0;
                        return;
                    }
                    todo.remove(current);
                }
            }
            if (current != 0) {
                curActionData = getNpcModel().get_v().getAction().getBehavior().get(current);
                if (curActionData != null) {
                    action = actions.get(curActionData.getAid());
                    if (curActionData.getCreateTime() == null) {
                        curActionData.setCreateTime(System.currentTimeMillis());
                    }
                }
                getNpcModel().get_v().getAction().setLastAction(curActionData);
            }
            if (action != null) {
                action.execute(this, curActionData);
                if ((curActionData.getStatus() != ActionStatus.TODO && action.checkEnd(this, curActionData)) || (System.currentTimeMillis() - curActionData.getCreateTime()) > GameConsts.MAX_PAUSE_TIME) {
                    current = 0;
                    curActionType = 0;
                    curActionData.setEndTime(System.currentTimeMillis());
                    finishAction(curActionData);
                    notifyActionEnd(curActionData, ActionStatus.COMPLELE);
                } else if (curActionData.getStatus() == ActionStatus.FAIL) {
                    current = 0;
                    curActionType = 0;
                    curActionData.setEndTime(System.currentTimeMillis());
                    finishAction(curActionData);
                    notifyActionEnd(curActionData, ActionStatus.FAIL);
                }
            }
        } catch (Exception e) {
            log.error("action执行异常", e);
            getNpcModel().get_v().getAction().getBehavior().clear();
            todo.clear();
            current = 0;
            curActionType = 0;
            getNpcDataListener().notifyProperty(true);
        }
    }

    public boolean checkPosition(Map<String, Object> objectMap, int roomId) {
        if (objectMap.containsKey("oid")) {
            String oid = (String) objectMap.get("oid");
            MapObject mapObject = findMapObj(oid, roomId);
            if (!RoomManager.getInstance().getMapManager(this.getRoomId()).canStand(this, mapObject.getGridX(), mapObject.getGridY())) {
                int distance = MapUtil.getDistance(getGridPostion(), new Position(mapObject.getGridX(), mapObject.getGridY()));
                return distance <= 3;
            }
            return getGridPostion().equals(new Position(mapObject.getGridX(), mapObject.getGridY()));
        } else if (objectMap.containsKey("npcId")) {
            NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(((Number)objectMap.get("npcId")).longValue());
            if (sellerNpc == null) {
                throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
            }
            NPC npc = sellerNpc.getNpc();
            if (this.distanceTo(npc) <= 64) {
                return true;
            }
            return false;
        } else if (objectMap.containsKey("gridX") && objectMap.containsKey("gridY")) {
            int gridX = ((Number)objectMap.get("gridX")).intValue();
            int girdY = ((Number)objectMap.get("gridY")).intValue();
            if (!RoomManager.getInstance().getMapManager(this.getRoomId()).canStand(this, gridX, girdY)) {
                return false;
            }
            return getGridPostion().equals(new Position(gridX, girdY));
        }
        return false;
    }

    public MapObject findMapObj(String oid, int roomId) {
        if (!StringUtils.isEmpty(oid)) {
            NpcRoom npcRoom = RoomManager.getInstance().getRoom(roomId);
            GameMap gameMap = npcRoom.getGameMap();
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

    public void notifyActionEnd(ActionData actionData, int status) {
        NpcActionResponse response = new NpcActionResponse();
        response.setNpcId(actionData.getNpcId());
        response.setActionId(actionData.getAid());
        response.setData(GsonUtil.toMap(actionData.getContent()));
        response.setStatus(actionData.getStatus());
        response.setActionUid(actionData.getId());
        response.setRequestId(RequestIDManager.getInstance().RequestID(false));
        log.debug("sendMessage NpcActionResponse ===================:{}", response.toString());
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPythonService, response);
    }

    public void handleReplyChat() {
        long toReplyChat = 0;
        ActionData actionData = null;
        try {
            for (long bid : todo) {
                actionData = getNpcModel().get_v().getAction().getBehavior().get(bid);
                if (actionData != null && actionData.getAid() == ActionEnumType.ReplyChat.getCode()) {
                    toReplyChat = bid;
                    break;
                }
            }
            if (toReplyChat != 0) {
                todo.remove(toReplyChat);
                Action action = actions.get(actionData.getAid());
                if (action != null) {
                    this.setLastSpeakTime(System.currentTimeMillis());
                    action.execute(this, actionData);
                    finishAction(actionData);
                }
                return;
            }
        } catch (Exception e) {
            log.error("replyAction执行异常", e);
            if (toReplyChat != 0) {
                todo.remove(toReplyChat);
                getNpcModel().get_v().getAction().getBehavior().remove(toReplyChat);
            }
        }
    }

    //事件触发
    @Override
    public void onEvent(Event event) {
        this.events.add(event);
    }

    //NPC创建后，初始化
    protected abstract void init();

    //NPC类型
    protected abstract CharacterType npcType();

    //注册NPC行为
    protected abstract void registAction();

    //初始化服务器的AI
    protected void initServerAi() {

    }

    //系统启动初始化正在做的行为
    protected void initAction() {
        Map<Long, ActionData> behavior = this.getNpcModel().get_v().getAction().getBehavior();
        Iterator<Map.Entry<Long, ActionData>> iter = behavior.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, ActionData> next = iter.next();
            ActionData data = next.getValue();
            Action action = this.actions.get(data.getAid());
            if (action == null) {
                iter.remove();
                continue;
            }
            if (data.getStatus() == 1) {
                iter.remove();
                action.addActionLog(data.getId(), this);
            } else if (data.getStatus() == 0) {
                this.current = data.getId();
            } else if (data.getStatus() == 2) {
                this.todo.add(data.getId());
            }
        }
    }

    public void doAction(int actionId, Map<String, Object> params, boolean serverAction) {
        Action action = this.actions.get(actionId);
        if (action != null) {
            if (action.getPreActions() != null) {
                for (Object obj : action.getPreActions()) {
                    Action actionTemp = (Action) obj;
                    Map<String, Object> tempParam = new HashMap<>();
                    tempParam.put("oid", actionTemp.getOid(params));
                    doAction(actionTemp.getActionType().getCode(), tempParam, serverAction);
                }
            }
            if (action.getOids() != null) {
                params.put("oid", action.getOid(params));
            }
            if (action.getParams() != null) {
                params.put("oid", action.getOid(params));
            }
            if (actionId == ActionEnumType.ReplyChat.getCode() || actionId == ActionEnumType.Move.getCode()) {
                this.todo.add(0, action.saveAction(this, params, 0, null, null, serverAction).getId());
            } else {
                this.todo.add(action.saveAction(this, params, 0, null, null, serverAction).getId());
            }
        } else {
            getNpcDataListener().notifyProperty(true);
        }
    }

    public void doAction(int actionId, NpcActionRequest request, NpcActionResponse actionResponse, boolean serverAction) {
        Action action = this.actions.get(actionId);
        if (action != null) {
            //处理oid冲突
            if (actionId == ActionEnumType.DrinkCoffee.getCode()) {
                List<String> list = new ArrayList<>(action.getOids());
                for (NpcHolder npcHolder : NpcManager.getInstance().getOnlineNpcMap().values()) {
                    NPC npc = npcHolder.getNpc();
                    if (StringUtils.isNotEmpty(npc.getCurOid())) {
                        list.remove(npc.getCurOid());
                    }
                    request.getData().put("oid", list.get(0));
                }
            }
            if (request.getData().containsKey("oid")) {
                this.curOid = request.getData().get("oid").toString();
            }
            long bid = action.saveAction(this, request.getData(), request.getDurationTime(), request.getSpeak(), request.getMood(), serverAction).getId();
            if (actionId == ActionEnumType.ReplyChat.getCode()) {
                this.todo.add(0, bid);
            } else {
                this.todo.add(bid);
            }
            actionResponse.setActionUid(bid);
            actionResponse.setStatus(ActionStatus.TODO);
        } else {
            getNpcDataListener().notifyProperty(true);
        }
    }

    /**
     * 行为结束
     *
     */
    public void finishAction(ActionData actionData) {
        Action action = this.actions.get(actionData.getAid());
        if (action != null) {
            action.afterDoing(this, actionData);
        }
    }

    public void clear() {
        this.current = 0;
        this.todo.clear();
        this.curActionType = 0;
        getNpcModel().get_v().getAction().getBehavior().clear();
        getNpcModel().get_v().setTalk(new NpcTalk());
    }

    public void addAction(Action action) {
        this.actions.putIfAbsent(action.getActionType().getCode(), action);
    }

    public void addCurrentAction(Action action) {
        this.currentPlan.add(action);
    }

    private void initPosition() {
        if (this.position == null) {
            PNpc npcModel = this.getNpcModel();
            this.position = new Position(npcModel.getX(), npcModel.getY());
        }
    }

    public void updatePosition(Integer x, Integer y) {

        boolean isUpdate = false;
        PNpc npcModel = this.getHolder().getNpcModel();

        Position lastPosition = new Position(this.getGridX(), this.getGridY());

        if (x != null && x >= 0) {
            this.position.setX(x);
            npcModel.setX(x);
            isUpdate = true;
        }

        if (y != null && y >= 0) {
            this.position.setY(y);
            npcModel.setY(y);
            isUpdate = true;
        }

        if (isUpdate) {
            RoomManager.getInstance().getMapManager(this.getRoomId()).updatePostion(this, lastPosition.getX(), lastPosition.getY());
            Threads.runAsync(ThreadConst.QUEUE_LOGIC, 0, "npc#position", () -> {
                //地图靠近事件、查找附件的人
                //MapDataManager.getInstance().getGameMap().eventManager.checkEvents(this);

                //同步数据
                //npcDataListener.notifyProperty(false);
            });
        }
    }

    //根据坐标计算距离，查找指定范围内的对象
    public double distanceTo(NPC npc) {
        double dx = this.position.x - npc.getPosition().x;
        double dy = this.position.y - npc.getPosition().y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public PNpc getNpcModel() {
        return this.getHolder().getNpcModel();
    }

    public Position getGridPostion() {
        return new Position(this.position.x / 32, this.position.y /32);
    }

    @Override
    public int getElementType() {
        return MapElementType.NPC.type;
    }

    @Override
    public int getGridX() {
        return this.position.x / 32;
    }

    @Override
    public int getGridY() {
        return this.position.y /32;
    }
}
