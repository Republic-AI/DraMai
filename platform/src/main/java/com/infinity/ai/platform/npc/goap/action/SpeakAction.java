package com.infinity.ai.platform.npc.goap.action;

import com.alibaba.fastjson.JSON;
import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.NpcTalkContent;
import com.infinity.ai.domain.tables.NpcTalk;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.SpeakData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.platform.npcdata.TalkData;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

//说话行动类
@Slf4j
public class SpeakAction extends Action<NpcActionRequest.SpeakData> {

    public SpeakAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.SpeakData params) {
        NpcHolder targetHolder;
        if (params.getNpcId() != null) {
            targetHolder  = NpcManager.getInstance().getOnlineNpcHolder(params.getNpcId());
        } else {
            targetHolder = npc.getHolder();
        }
        NPC targetHpc = targetHolder.getNpc();
        return SpeakData.builder().sender(npc.id)
                .sName(npc.name)
                .target(params.npcId)
                .tName(targetHpc.name)
                .time(System.currentTimeMillis())
                .content(params.content)
                .build()
                .toJsonString();
    }

    public SpeakAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Speak;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.SpeakData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.SpeakData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.SpeakData params) {
        log.debug("SpeakAction perform,npcId={}", npc.getId());
        long targetNpcId = params.getNpcId() == null ? npc.getId() : params.getNpcId();
        //广播给所有客户端
        sendMessage(npc, "npcId", targetNpcId, "content", params.getContent());

        /*actionData.getParams().put("npcId", targetNpcId);

        //正在说话
        NpcTalk talk = npc.getNpcModel().get_v().getTalk();
        talk.setTalking(true);

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
        targetTalkContent.setContent(params.getContent());*/


    }

    @Override
    public void afterPerform(NPC npc, ActionData params) {
        log.debug("afterPerform npc={}, bid={}, params={}", npc.getId(), params, params.getId());
        /*ActionData actionData = npc.getNpcModel().get_v().getAction().getBehavior().get(params.getId());
        if (actionData == null) {
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
        }*/
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
            //npc.getNpcDataListener().notifyProperty(false);
        }
    }

}


