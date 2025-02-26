package com.infinity.ai.platform.npc.data;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.domain.model.NpcTalkContent;
import com.infinity.ai.domain.tables.NpcTalk;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.character.CharacterType;
import com.infinity.ai.platform.npc.goap.action.Action;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.data.NpcCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.msg.platform.npcdata.*;
import com.infinity.common.utils.GsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

//NPC数据类
@Getter
@Slf4j
public class NpcDataListener {
    public NPC npc;
    //NPC数据
    public NpcData npcData;
    //属性变动监控
    public PropertyChangeSupport support;
    //是否正在发送数据：true:是，false:否
    public volatile boolean isSync;

    public NpcDataListener(NPC npc) {
        this.npc = npc;
        this.npcData = new NpcData(this.npc.getId());
        support = new PropertyChangeSupport(this);
        this.addPropertyChangeListener(NpcManager.getInstance().getListener());
    }

    public void initNpcData() {
        //this.npcData.setInfo(buildInfoData(npc.getId().intValue()));
        this.actionData();
        this.curAction();
        this.npcData.setItems(this.getItemsData());
        this.npcData.setSelling(this.getSellingData());
        this.npcData.setMapData(this.buildMapData());
        this.talk();
    }

    private void reload() {
        this.actionData();
        this.curAction();
        this.npcData.setItems(this.getItemsData());
        this.npcData.setSelling(this.getSellingData());
        this.talk();
    }

    private void curAction() {
        ActionDataVo actionDataVo = npcData.getCurAction();
        if (npc.getTodo().isEmpty()) {
            actionDataVo.actionId = null;
            actionDataVo.actionName = null;
            actionDataVo.param = null;
            return;
        }
        ActionData actionData = npc.getNpcModel().get_v().getAction().getBehavior().get(npc.getTodo().get(0));
        if (actionData == null) {
            actionDataVo.actionId = null;
            actionDataVo.actionName = null;
            actionDataVo.param = null;
        } else {
            Action action = npc.getActions().get(actionData.getAid());
            ActionEnumType actionType = action.getActionType();
            actionDataVo.actionId = actionType.getCode();
            actionDataVo.actionName = actionType.getName();
            actionDataVo.param = GsonUtil.toMap(actionData.getContent());
        }
    }

    public void loadItems() {
        this.npcData.setItems(this.getItemsData());
        //this.npcData.setSelling(this.getSellingData());
    }

    //NPC基本信息
    public InfoData buildInfoData(int npcId) {
        NpcCfg npcCfg = GameConfigManager.getInstance().getNpcCfgManager().get(npcId);
        return InfoData.builder()
                .name(npcCfg.getName())
                .firstName(npcCfg.getFirstName())
                .lastName(npcCfg.getLastName())
                .type(CharacterType.getByCode(npcCfg.getType()).get().getName())
                .age(npcCfg.getAge())
                .height(npcCfg.getHeight())
                .weight(npcCfg.getWeight())
                .body_style(npcCfg.getBodyStyle())
                .innate(npcCfg.getInnate())
                .learned(npcCfg.getLearned())
                .lifestyle(npcCfg.getLifestyle())
                .living_area(npcCfg.getLivingArea())
                .property(npcCfg.getProperty())
                .build();
    }

    //售卖物品 (如果有)
    public List<SellingData> getSellingData() {
        NpcHolder holder = NpcManager.getInstance().getOnlineNpcHolder(this.npc.getId());
        if (holder != null) {
            return Collections.EMPTY_LIST;
        }

        List<SellingData> sellingData = new ArrayList<>();
        ItemBaseDataManager itemCfgMgr = GameConfigManager.getInstance().getItemBaseDataManager();
        List<Goods> all = holder.getBag().getAll();
        all.stream().forEach(goods -> {
            ItemCfg itemCfg = itemCfgMgr.getItemConfigWithID(goods.goodsId);
            if (itemCfg != null) {
                sellingData.add(SellingData.builder().name(itemCfg.Name).itemId(itemCfg.Id).count(goods.count).build());
            }
        });
        return sellingData;
    }

    //NPC当前行为
    public void actionData() {
        ActionDataVo actionDataVo = this.npcData.getAction();
        ActionData lastAction = this.npc.getNpcModel().get_v().getAction().getLastAction();
        if (lastAction == null || lastAction.getAid() == null || lastAction.getAid() == 0) {
            actionDataVo.actionId = null;
            actionDataVo.actionName = null;
            actionDataVo.param = null;
            return;
        }

        Action action = npc.getActions().get(lastAction.getAid());
        ActionEnumType actionType = action.getActionType();
        actionDataVo.actionId = actionType.getCode();
        actionDataVo.actionName = actionType.getName();
        actionDataVo.param = GsonUtil.toMap(lastAction.getContent());
    }

    //NPC 拥有的物品
    public List<ItemsData> getItemsData() {
        NpcHolder holder = NpcManager.getInstance().getOnlineNpcHolder(this.npc.getId());
        if (holder == null) {
            return Collections.EMPTY_LIST;
        }

        List<ItemsData> sellingData = new ArrayList<>();
        ItemBaseDataManager itemCfgMgr = GameConfigManager.getInstance().getItemBaseDataManager();
        List<Goods> all = holder.getBag().getAll();
        all.stream().forEach(goods -> {
            ItemCfg itemCfg = itemCfgMgr.getItemConfigWithID(goods.goodsId);
            if (itemCfg != null && goods.count > 0) {
                sellingData.add(ItemsData.builder().name(itemCfg.Name).itemId(itemCfg.Id).count(goods.count).build());
            }
        });
        return sellingData;
    }

    //地图物品数据
    public List<MapData> buildMapData() {
        Map<String, List<MapObject>> mapObjects = MapDataManager.getInstance().getGameMap().getByNpcId(this.npc.getId().intValue());
        if (mapObjects == null || mapObjects.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<MapData> mapDataList = new ArrayList<>();
        for (Map.Entry<String, List<MapObject>> entry : mapObjects.entrySet()) {
            List<MapObject> mapObjectList = entry.getValue();
            if (mapObjectList == null || mapObjectList.size() == 0) {
                continue;
            }

            for (MapObject mapObject : mapObjectList) {
                if (mapObject == null) {
                    continue;
                }

                mapDataList.add(MapData.builder()
                        .objName(mapObject.name)
                        .oid(mapObject.name)
                        .type(mapObject.type)
                        .X(mapObject.x)
                        .Y(mapObject.y)
                        .build());
            }
        }
        return mapDataList;
    }

    //周边信息
    public void watchSurroundings() {
        //周边的地理位置信息

        //周边的人

        //周边的物品信息

    }

    //当前NPC对话信息，多人对话
    public void talk() {
        TalkData talkData = this.npcData.getTalk();
        NpcTalk talk = npc.getNpcModel().get_v().getTalk();
        if (!talk.isTalking()) {
            talkData.setTalking(false);
            talkData.setTalkingTo(null);
            talkData.setContents(null);
            return;
        }

        talkData.setTalking(talk.isTalking());
        Set<Long> talkingTo = new HashSet<>();//Optional.ofNullable(talkData.getTalkingTo()).orElse(new HashSet<>());
        List<TalkData.Contents> contents = new ArrayList<>();//Optional.ofNullable(talkData.getContents()).orElse(new ArrayList<>());

        //对我说的
        Map<Long, NpcTalkContent> toMe = talk.getToMe();
        toMe.forEach((npcId, content) -> {
            talkingTo.add(npcId);
            TalkData.Contents ctt = TalkData.Contents.builder()
                    .sender(content.sender)
                    .target(content.target)
                    .time(content.time)
                    .content(content.content)
                    .build();
            contents.add(ctt);
        });

        //我说的
        /*Map<Long, NpcTalkContent> meSay = talk.getMeSay();
        meSay.forEach((npcId, content) -> {
            talkingTo.add(npcId);
            TalkData.Contents ctt = TalkData.Contents.builder()
                    .sender(content.sender)
                    .target(content.target)
                    .time(content.time)
                    .content(content.content)
                    .build();
            contents.add(ctt);
        });*/

        talkData.setTalkingTo(talkingTo);
        talkData.setContents(contents);
    }

    public NpcData getNpcData() {
        talk();
        return npcData;
    }

    //NPC数据变动通知
    public void notifyProperty(boolean reload) {
        if (isSync) {
            log.debug("notifyProperty error, Currently sending data,reload={}", reload);
            return;
        }

        if (reload) {
            reload();
            ActionEnumType curAction = ActionEnumType.getByCode(npc.getCurActionType()).orElse(null);
            if (curAction != null && curAction != ActionEnumType.Move) {
                npcData.status = curAction.getName();
            } else if (curAction == ActionEnumType.Move && npcData.curAction.actionName != null) {
                npcData.status = npcData.curAction.actionName;
            } else if (curAction == ActionEnumType.Move){
                npcData.status = curAction.getName();
            } else {
                npcData.status = "free";
            }
        }
        isSync = true;
        support.firePropertyChange("npcData", null, npcData);
    }

    public void setNpcData(NpcData npcData) {
        support.firePropertyChange("npcData", npcData, npcData);
        this.npcData = npcData;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
