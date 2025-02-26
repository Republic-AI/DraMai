package com.infinity.ai.platform.npc.goap.action;

import com.alibaba.fastjson.JSON;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.Chat;
import com.infinity.ai.domain.model.NpcTalkContent;
import com.infinity.ai.domain.tables.NpcTalk;
import com.infinity.ai.platform.manager.ChatManager;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.NpcTalkData;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.platform.npcdata.TalkData;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

//说话行动类
@Slf4j
public class TalkAction extends Action<NpcActionRequest.NpcTalkData> {

    public TalkAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.NpcTalkData params) {
        if (params.getNpcId() == null) {
            log.error("NpcTalkData npcId not found, npcId = {}", npc.getId());
            throw new BusinessException(ResultCode.FAILURE);
        }
        NpcHolder targetHolder;
        if (params.getNpcId() != null) {
            targetHolder  = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        } else {
            targetHolder = npc.getHolder();
        }
        NPC targetHpc = targetHolder.getNpc();
        return NpcTalkData.builder().sender(npc.id)
                .sName(npc.name)
                .npcId(params.npcId)
                .tName(targetHpc.name)
                .time(System.currentTimeMillis())
                .content(params.content)
                .endingTalk(params.endingTalk)
                .build()
                .toJsonString();
    }

    public TalkAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Talk;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.NpcTalkData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.NpcTalkData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.NpcTalkData params) {
        log.debug("SpeakAction perform,npcId={}", npc.getId());
        long targetNpcId = params.getNpcId();
        //广播给所有客户端
        //sendMessage(npc, "npcId", targetNpcId, "content", params.getContent());
        sendActionMessage(npc, actionData, ActionEnumType.Speak.getCode(), "npcId", targetNpcId, "content", params.getContent());
        ChatManager.getInstance().talk(npc.getId(), targetNpcId, params.getContent());
        if (params.getEndingTalk() == 1 || npc.getId() == targetNpcId) {
            cutTalk(npc, targetNpcId);
            return;
        }

        //正在说话
        NpcTalk talk = npc.getNpcModel().get_v().getTalk();

        int count = talk.getCount().getOrDefault(targetNpcId, 0);
        if (count >= GameConsts.TALK_COUNT) {
            cutTalk(npc, targetNpcId);
            return;
        }
        talk.getCount().put(targetNpcId, count + 1);

        actionData.getParams().put("npcId", targetNpcId);

        talk.setTalking(true);
        talk.getToMe().remove(targetNpcId);


        //我对别的NPC说的
        NpcTalkContent talkContent = talk.getMeSay().get(targetNpcId);
        if (talkContent == null) {
            talkContent = new NpcTalkContent();
            talk.getMeSay().putIfAbsent(targetNpcId, talkContent);
        }

        talkContent.setSender(npc.id);
        talkContent.setTarget(targetNpcId);
        talkContent.setTime(System.currentTimeMillis());
        talkContent.setContent(params.getContent());

        //把聊天记录写入对方对话中
        NpcHolder targetHolder = NpcManager.getInstance().getOnlineNpcHolder(targetNpcId);
        NpcTalk targetTalk = targetHolder.getNpc().getNpcModel().get_v().getTalk();
        NpcTalkContent targetTalkContent = targetTalk.getToMe().get(npc.getId());
        if (targetTalkContent == null) {
            targetTalkContent = new NpcTalkContent();
            targetTalk.getToMe().putIfAbsent(npc.getId(), targetTalkContent);
        }

        targetTalk.setTalking(true);
        targetTalkContent.setSender(npc.id);
        targetTalkContent.setTarget(targetNpcId);
        targetTalkContent.setTime(System.currentTimeMillis());
        targetTalkContent.setContent(params.getContent());
    }

    public void cutTalk(NPC npc, long targetNpcId) {
        for (long temp : npc.getNpcModel().get_v().getTalk().getToMe().keySet()) {
            NpcHolder targetHolder = NpcManager.getInstance().getOnlineNpcHolder(temp);
            targetHolder.getNpc().getNpcModel().get_v().setTalk(new NpcTalk());
            targetHolder.getNpc().getNpcModel().get_v().getAction().setLastAction(null);
            targetHolder.getNpc().getNpcDataListener().notifyProperty(true);
        }
        npc.getNpcModel().get_v().setTalk(new NpcTalk());
        npc.getNpcModel().get_v().getAction().setLastAction(null);
        npc.getNpcDataListener().notifyProperty(true);
        ChatManager.getInstance().endTalk(npc.getId(), targetNpcId);
    }

    @Override
    public void beforePerform(NPC npc, ActionData data) {
        /*NpcActionRequest.NpcTalkData params = getParamData(data);
        if (!params.isInitiator()) {
            return;
        }
        NpcHolder sellerNpc = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        if (sellerNpc == null) {
            throw new BusinessException(ResultCode.NPC_NOT_EXIST_ERROR);
        }
        NPC seller = sellerNpc.getNpc();
        //获取自己周围一个可用点
        Position myPos = npc.getGridPostion();
        List<Position> positionList = MapDataManager.getInstance().getGameMap().aStar.getNeighbors(myPos);
        if (positionList == null || positionList.isEmpty()) {
            throw new BusinessException("position not found");
        }
        Position temp = positionList.get(0);
        Map<String, Object> par = new HashMap<>();
        par.put("npcId", npc.getId());
        par.put("gridX", temp.getX());
        par.put("gridY", temp.getY());
        seller.doAction(ActionEnumType.Talk.getCode(), par);*/
    }

    @Override
    public void afterPerform(NPC npc, ActionData actionData) {
        log.debug("afterPerform npc={}, bid={}, params={}", npc.getId(), params, actionData.getId());
        NpcActionRequest.NpcTalkData talkData = getParamData(actionData);

        if (talkData.getEndingTalk() == 1) {
            return;
        }

        Map<String, Object> params1 = actionData.getParams();
        log.debug("actionData = {}", JSON.toJSONString(actionData));
        if (params1 != null) {
            Object npcId = params1.get("npcId");
            if (npcId != null) {
                NpcHolder targetHolder = NpcManager.getInstance().getOnlineNpcHolder((long) npcId);
                //targetHolder.getNpc().getNpcDataListener().notifyProperty(false);
                talk(npc.getId(), targetHolder.getNpc());
            }
        }
    }

    public void talk(Long npcId, NPC npc) {
        TalkData talkData = npc.getNpcDataListener().getNpcData().getTalk();
        NpcTalk talk = npc.getNpcModel().get_v().getTalk();
        if (!talk.isTalking()) {
            talkData.setTalking(false);
            talkData.setTalkingTo(null);
            talkData.setContents(null);
            return;
        }

        talkData.setTalking(talk.isTalking());
        Set<Long> talkingTo = new HashSet<>();
        List<TalkData.Contents> contents = new ArrayList<>();

        //对我说的
        Map<Long, NpcTalkContent> toMe = talk.getToMe();
        NpcTalkContent content = toMe.get(npcId);
        if (content != null) {
            talkingTo.add(npcId);
            TalkData.Contents ctt = TalkData.Contents.builder()
                    .sender(content.sender)
                    .target(content.target)
                    .time(content.time)
                    .content(content.content)
                    .build();
            contents.add(ctt);

            talkData.setTalkingTo(talkingTo);
            talkData.setContents(contents);
            npc.getNpcDataListener().notifyProperty(true);
        }
    }

}


