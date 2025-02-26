package com.infinity.common.config.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameConfigManager implements IResource {
    private static class ConfigManagerHolder {
        private static final GameConfigManager configManager = new GameConfigManager();
    }

    public static GameConfigManager getInstance() {
        return ConfigManagerHolder.configManager;
    }

    private String configPath = null;

    @Getter
    @Setter
    private TaskDataManager taskDataManager;
    @Getter
    @Setter
    private ItemBaseDataManager itemBaseDataManager;
    @Getter
    @Setter
    private DropBaseDataManager dropBaseData;
    @Getter
    @Setter
    private SysParamCfgManager sysParamCfgManager;
    @Getter
    @Setter
    private ShopCfgDataManager shopCfgDataManager;

    @Getter
    @Setter
    private NpcCfgManager npcCfgManager;
    @Getter
    @Setter
    private NpcActionCfgManager npcActionCfgManager;
    @Getter
    @Setter
    private NpcItemCfgManager npcItemCfgManager;
    @Getter
    @Setter
    private NpcTypeCfgManager npcTypeCfgManager;
    @Getter
    @Setter
    private GiftCfgManager giftCfgManager;
    @Getter
    @Setter
    private JoinCfgManager joinCfgManager;


    private GameConfigManager() {
        //PolitenessExit.addResource(this);
    }

    public void dispose() {
        //PolitenessExit.removeResource(this);
    }

    public void loadConfig(final String basePath) {
        configPath = basePath;
        reload();
    }

    @Override
    public void reload() {
        assert (configPath != null || configPath.isEmpty()) : "config path is null or empty.";

        final String kTaskConfig = "TaskCfg.json";
        this.setTaskDataManager(new TaskDataManager(configPath, kTaskConfig));
        log.info("load {} done.", kTaskConfig);

        final String kItemBaseDataConfigName = "ItemCfg.json";
        this.setItemBaseDataManager(new ItemBaseDataManager(configPath, kItemBaseDataConfigName));
        log.info("load {} done.", kItemBaseDataConfigName);

        final String kSysParamConfigName = "SysParameter.json";
        this.setSysParamCfgManager(new SysParamCfgManager(configPath, kSysParamConfigName));
        log.info("load {} done.", kSysParamConfigName);

        final String kDropCfg = "DropsCfg.json";
        this.setDropBaseData(new DropBaseDataManager(configPath, kDropCfg));
        log.info("load {} done.", kSysParamConfigName);

        final String kShopCfg = "ShopCfg.json";
        this.setShopCfgDataManager(new ShopCfgDataManager(configPath, kShopCfg));
        log.info("load {} done.", kShopCfg);

        final String kNpcCfg = "NpcCfg.json";
        this.setNpcCfgManager(new NpcCfgManager(configPath, kNpcCfg));
        log.info("load {} done.", kNpcCfg);

        final String kNpcActionCfg = "NpcActionCfg.json";
        this.setNpcActionCfgManager(new NpcActionCfgManager(configPath, kNpcActionCfg));
        log.info("load {} done.", kNpcActionCfg);

        final String kNpcItemCfg = "NpcItemCfg.json";
        this.setNpcItemCfgManager(new NpcItemCfgManager(configPath, kNpcItemCfg));
        log.info("load {} done.", kNpcItemCfg);

        final String kNpcTypeCfg = "NpcTypeCfg.json";
        this.setNpcTypeCfgManager(new NpcTypeCfgManager(configPath, kNpcTypeCfg));
        log.info("load {} done.", kNpcTypeCfg);

        final String kGiftCfg = "GiftCfg.json";
        this.setGiftCfgManager(new GiftCfgManager(configPath, kGiftCfg));
        log.info("load {} done.", kNpcTypeCfg);

        final String joinCfg = "JoinCfg.json";
        this.setJoinCfgManager(new JoinCfgManager(configPath, joinCfg));
        log.info("load {} done.", joinCfg);
    }

}
