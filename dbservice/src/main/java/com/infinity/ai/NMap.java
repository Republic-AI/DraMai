package com.infinity.ai;

import com.infinity.ai.domain.tables.VMap;
import com.infinity.db.db.DBEntity;
import lombok.Data;
import lombok.ToString;

//地图数据
@Data
@ToString
public class NMap implements DBEntity<VMap> {
    //记录ID
    private long id;
    //名称
    private String name;
    //详细
    private transient byte[] v;

    private VMap _v = new VMap();

    public NMap() {
        super();
    }

    @Override
    public String getTableName() {
        return "game_data";
    }

    @Override
    public int getCacheExpire() {
        return 60;
    }

    @Override
    public Class<VMap> ref_v() {
        return VMap.class;
    }

    public static Long MAPID() {
        return 1000L;
    }
}
