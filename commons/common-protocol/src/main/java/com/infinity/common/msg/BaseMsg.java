package com.infinity.common.msg;

import com.infinity.common.utils.GsonUtil;

import java.util.List;

public class BaseMsg<T> {
    //请求ID
    protected long requestId;

    //玩家ID
    private Long playerId;

    //玩家no
    private String userNo;

    //需要广播的列表
    private List<Long> playerIds;

    //服务类型：1：platform
    protected int type;

    //各接口命令编号
    protected int command;

    //=================================
    //错误码,0：成功,其他失败
    protected int code = 0;

    //错误信息
    protected String message;

    //会话ID
    protected String sessionId;

    private String gateway;

    private String ip;

    protected T data;


    public BaseMsg() {
        super();
        this.command = getCommand();
        this.type = getType();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public int getType() {
        return 0;
    }

    public void setType(int type) {
        this.type = getType();
    }

    public int getCommand() {
        return 0;
    }


    public void setCommand(int command) {
        this.command = command;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Long getPlayerId() {
        return playerId == null ? 0 : playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public void clear() {
        this.playerId = null;
        this.gateway = null;
        this.sessionId = null;
        this.playerIds = null;
    }
}
