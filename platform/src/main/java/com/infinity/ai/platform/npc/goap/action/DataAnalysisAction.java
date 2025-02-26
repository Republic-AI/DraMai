package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.TypeData;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 打字行动类
@Slf4j
public class DataAnalysisAction extends CommonAction {

    public DataAnalysisAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.DataAnalysis;
    }

}


