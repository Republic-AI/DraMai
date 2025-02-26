package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.ThinkData;
import com.infinity.ai.platform.npc.goap.action.data.TidyUpData;
import com.infinity.common.consts.GameConsts;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 整理货架行动类
@Slf4j
public class TidyUpAction extends CommonAction {


    public TidyUpAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public TidyUpAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.TidyUp;
    }

}


