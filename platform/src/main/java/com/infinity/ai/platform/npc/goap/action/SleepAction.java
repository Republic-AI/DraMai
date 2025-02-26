package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.SleepData;
import com.infinity.ai.platform.task.timer.ExpireMsgBuilder;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.timer.SubmitExpridedMessage;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// 睡眠行动类
@Slf4j
public class SleepAction extends Action<NpcActionRequest.SleepData> {

    public SleepAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.SleepData params) {
        return SleepData.builder().oid(params.oid).build().toJsonString();
    }

    public SleepAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Sleep;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.SleepData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, NpcActionRequest.SleepData params) {
        log.debug("SleepAction perform,npcId={}", npc.getId());
        //获取床的坐标
        MapObject mapObject = findMapObj(params.getOid());

        //广播给所有客户端
        sendMessage(npc, "oid", mapObject.name);
        //Sequence sequence = new Sequence();
        //sequence.addChild(new MoveActionNode(new Position(mapObject.x, mapObject.y)));
        //sequence.addChild(new SleepActionNode(new Position(mapObject.x, mapObject.y)));
        //npc.setBehaviorTree(sequence);

        //起床时间到了，自动起床
        long getUpTime = getGetUptime(params.getUpTime);
        if (getUpTime > 0) {
            log.debug("expire getUpTime={},delay={}", System.currentTimeMillis() + getUpTime, getUpTime);
            NpcActionRequest data = new NpcActionRequest();
            data.setActionId(ActionEnumType.GetUp.getCode());
            data.setNpcId(npc.getId());
            Map<String, Object> inParams = new HashMap<>();
            inParams.put("oid", mapObject.name);
            data.setData(inParams);

            SubmitExpridedMessage msg = ExpireMsgBuilder.buildMsg(ProtocolCommon.QUARTZ_GETUP_COMMAND, getUpTime, npc.getId());
            msg.setData(data);
            log.debug("Send message to Quartz, msg={}", msg.toString());
            MessageSender.getInstance().sendMessage(NodeConstant.kQuartzService, msg);
        }
    }

    //计算起床时间
    private Long getGetUptime(String getUpTimeStr) {
        if (StringUtils.isEmpty(getUpTimeStr)) {
            return 0L;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(getUpTimeStr, formatter);
        long getUpTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //根据游戏时间，推算系统真实时间
        Long realGetUpTime = MapDataManager.getTimeFromGameTime(getUpTime);
        return realGetUpTime - System.currentTimeMillis();
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.SleepData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.SleepData params) {
        log.debug("TypeAction perform,npcId={}", npc.getId());
        //获取目标对象坐标
        MapObject mapObject = findMapObj(params.getOid());
        sendMessage(npc, actionData, null, "oid", mapObject.getName());
    }

}


