package com.infinity.ai.platform.map.object;

import com.infinity.ai.platform.map.event.MapEvent;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.consts.GameConsts;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// 地图物件类
@Data
public class MapObject {
    public String id;
    public String name;
    public String type;
    public int x;
    public int y;
    public int width;
    public int height;
    //自定义属性
    public Map<String, Object> properties;
    //关联的事件
    private List<MapEvent> events;
    //物品状态
    private Enum<?> state;

    public void checkEventTrigger(NPC npc) {
        if (events == null || events.size() == 0) {
            return;
        }

        //触发事件
        for (MapEvent event : events) {
            if (event != null) {
                event.onTrigger(this, npc);
            }
        }
    }

    //计算地图上两个对象的距离,查找指定范围内的对象
    public double distanceTo(MapObject other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    //根据坐标计算距离，查找指定范围内的对象
    public double distanceTo(int x, int y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    //根据坐标计算距离，查找指定范围内的对象
    public double distanceTo(NPC npc) {
        double dx = this.x - npc.getPosition().x;
        double dy = this.y - npc.getPosition().y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getGridX() {
        return x / GameConsts.GRID_X;
    }

    public int getGridY() {
        return y / GameConsts.GRID_Y;
    }
}