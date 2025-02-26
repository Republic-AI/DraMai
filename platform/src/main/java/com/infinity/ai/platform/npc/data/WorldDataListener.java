package com.infinity.ai.platform.npc.data;

import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.common.msg.platform.npcdata.WorldData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// 世界数据类
public class WorldDataListener {
    public WorldData worldData;

    private PropertyChangeSupport support;

    public WorldDataListener() {
        support = new PropertyChangeSupport(this);
        worldData = new WorldData();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setWorldData(WorldData worldData) {
        support.firePropertyChange("worldData", this.worldData, worldData);
        this.worldData = worldData;
    }

    public Long getTime() {
        Long time = MapDataManager.getInstance().getGameTime();
        return time;
    }

    public WorldData getWorldData() {
        worldData.time = getTime();
        return worldData;
    }
}