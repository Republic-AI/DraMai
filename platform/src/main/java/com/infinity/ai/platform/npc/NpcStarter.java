package com.infinity.ai.platform.npc;

import com.alibaba.fastjson.JSONObject;
import com.infinity.ai.PNpc;
import com.infinity.ai.platform.manager.*;
import com.infinity.common.config.data.NpcCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.ItemBaseDataManager;
import com.infinity.common.config.manager.NpcCfgManager;
import com.infinity.common.config.manager.NpcItemCfgManager;
import com.infinity.common.consts.GoodsSource;
import com.infinity.db.db.DBManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class NpcStarter {

    //创建系统的NPC
    public void initializeNpc() {
        try {
            //初始化地图信息
            log.info("load game map data......");
            MapDataManager.getInstance().createGameMap();
            log.info("load game map data done");

            log.info("init Game map data......");
            MapDataManager.getInstance().init();
            log.info("init Game map data done");

            log.info("start init system npc.......");
            NpcCfgManager npcCfgManager = GameConfigManager.getInstance().getNpcCfgManager();
            List<NpcCfg> npcCfgs = npcCfgManager.allNpcCfg();
            for (int i = 0; i < npcCfgs.size(); i++) {
                NpcCfg npcCfg = npcCfgs.get(i);
                if (npcCfg == null || npcCfg.getType() == 0 || npcCfg.getId() < 10006) {
                    continue;
                }

                PNpc npc = DBManager.get(PNpc.class, npcCfg.getId());
                if (npc == null) {
                    npc = newDBNpc(npcCfg);
                    DBManager.add(npc);
                    NpcManager.getInstance().addOnlineNpc(npc.getId(), npc);
                }

                //启动运行npc
                start(npc);

                NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcCfg.getId());
                NpcBagManager bagManager = npcHolder.getBag();
                NpcItemCfgManager npcItemCfgManager = GameConfigManager.getInstance().getNpcItemCfgManager();
                ItemBaseDataManager itemBaseDataManager = GameConfigManager.getInstance().getItemBaseDataManager();
                JSONObject itemCfg = npcItemCfgManager.get(npcCfg.getId());
                if (itemCfg == null) {
                    continue;
                }
                for (Map.Entry<String, Object> entry : itemCfg.entrySet()) {
                    if (entry.getKey().equals("id")) {
                        continue;
                    }
                    int id = itemBaseDataManager.getItemConfigWithName(entry.getKey()).getId();
                    int count = Integer.parseInt(entry.getValue().toString());
                    bagManager.addGoods(id, count, false, true, GoodsSource.NPC_INIT);
                }
            }
            log.info("init system npc done！");

            //初始化NPC周边信息
            log.info("check npc surroundings.......");
            surroundings(npcCfgs);
            log.info("check npc surroundings done");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void start(PNpc dbNPC) {
        if (dbNPC == null) {
            log.error("start npc error, PNpc is null");
            return;
        }

        //管理npc
        NpcManager npcManager = NpcManager.getInstance();
        NpcHolder npcHolder = npcManager.addOnlineNpc(dbNPC.getId(), dbNPC);

        //创建不同的NPC
        NPC npc = NPCFactory.createNPC(dbNPC);
        npcHolder.setNpc(npc);
        npc.setHolder(npcHolder);
        npcManager.addNPC(npc);

        //顺序不要动
        npc.initialize();
        npc.start();
    }

    public PNpc newDBNpc(NpcCfg npcCfg) {
        PNpc dbData = new PNpc();
        dbData.setId(npcCfg.getId());
        dbData.setPlayerId(0);
        //角色模型
        dbData.setModel(npcCfg.getModel());
        //角色名称
        dbData.setName(npcCfg.getName());
        //职业
        dbData.setCareer("");
        //关键词
        dbData.setKeyword("");
        //发型
        dbData.setHair(npcCfg.getHair());
        //top
        dbData.setTop(npcCfg.getTop());
        //bottoms
        dbData.setBottoms(npcCfg.getBottoms());
        dbData.setX(npcCfg.getPositionX());
        dbData.setY(npcCfg.getPositionY());
        dbData.setSpeed(0);
        dbData.setCreatedate(System.currentTimeMillis());
        dbData.setEndTime(0);
        dbData.setType(npcCfg.getType());
        return dbData;
    }

    public void reload() {
        try {
            //初始化地图信息
            log.info("load game map data......");
            MapDataManager.getInstance().createGameMap();
            log.info("load game map data done");

            log.info("start init system npc.......");
            NpcCfgManager npcCfgManager = GameConfigManager.getInstance().getNpcCfgManager();
            List<NpcCfg> npcCfgs = npcCfgManager.allNpcCfg();
            for (int i = 0; i < npcCfgs.size(); i++) {
                NpcCfg npcCfg = npcCfgs.get(i);
                if (npcCfg == null || npcCfg.getType() == 0) {
                    continue;
                }

                PNpc npc = DBManager.get(PNpc.class, npcCfg.getId());
                if (npc != null) {
                    continue;
                }

                npc = newDBNpc(npcCfg);
                DBManager.add(npc);
                NpcManager.getInstance().addOnlineNpc(npc.getId(), npc);

                //启动运行npc
                start(npc);
            }
            log.info("init system npc done！");

            //初始化NPC周边信息
            log.info("check npc surroundings.......");
            surroundings(npcCfgs);
            log.info("check npc surroundings done");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //检测NPC周边信息
    private void surroundings(List<NpcCfg> npcCfgs) {
        for (int i = 0; i < npcCfgs.size(); i++) {
            NpcCfg npcCfg = npcCfgs.get(i);
            if (npcCfg == null || npcCfg.getType() == 0) {
                continue;
            }

            NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcCfg.getId());
            if (npcHolder != null) {
                NPC npc = npcHolder.getNpc();
                MapDataManager.getInstance().getGameMap().eventManager.checkEvents(npc);
            }
        }
    }

    private static class Holder {
        private static final NpcStarter kInstance = new NpcStarter();
    }

    public static NpcStarter getInstance() {
        return NpcStarter.Holder.kInstance;
    }
}
