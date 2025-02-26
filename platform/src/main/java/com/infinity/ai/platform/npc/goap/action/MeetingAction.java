package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

// 打字行动类
@Slf4j
public class MeetingAction extends CommonAction {

    public MeetingAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public MeetingAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Meeting;
    }

}


