package com.infinity.ai;


import com.infinity.ai.domain.tables.VPlayer;
import com.infinity.db.db.DBEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 玩家
 */
@Data
@ToString
public class PPlayer implements DBEntity<VPlayer> {
    //ID
    private long id;
    //登录名
    private String name;
    //昵称
    private String nickname;
    //登录密码
    private String pwd;
    //玩家ID
    private String userno;
    //玩家头像
    //private String avatar;
    //性别 0:未知，1:男,2女
    //private int sex;
    //登录IP地址:端口
    private String loginip;
    //最后登录时间
    private long lasttime;
    //上一次退出时间
    private long lastofftime;
    //创建时间
    private long createdate;

    //状态：0=正常,1=封号
    private int status;

    //是否GM
    private int gm;

    //详细
    private transient byte[] v;

    private VPlayer _v = new VPlayer();

    public PPlayer() {
        super();
    }

    @Override
    public String getTableName() {
        return "player";
    }

    @Override
    public int getCacheExpire() {
        return 60;
    }

    @Override
    public Class<VPlayer> ref_v() {
        return VPlayer.class;
    }

    public boolean checkStatus(int status) {
        return this.status == status;
    }
}