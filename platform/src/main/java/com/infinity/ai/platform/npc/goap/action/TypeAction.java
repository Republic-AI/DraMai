package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.manager.NpcBagManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.FeedingData;
import com.infinity.ai.platform.npc.goap.action.data.ItemData;
import com.infinity.ai.platform.npc.goap.action.data.TypeData;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.consts.ActionStatus;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// 打字行动类
@Slf4j
public class TypeAction extends CommonAction {

    public TypeAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public TypeAction(List<Action> preActions) {
        super(preActions);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.Type;
    }

}


