package com.infinity.common.base.data;

import java.io.Serializable;

import com.infinity.common.cache.CacheObj;
import com.infinity.common.consts.CachePrefixConsts;

public class GameUser extends CacheObj implements Serializable {
	private static final long serialVersionUID = 7312914784693240088L;
	private long userId;
	private String userNo;
	private String nickName;
	private String gatewayServiceId;
	private String platformServiceId;
	private String chatServiceId;
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getGatewayServiceId() {
		return gatewayServiceId;
	}

	public void setGatewayServiceId(String gatewayServiceId) {
		this.gatewayServiceId = gatewayServiceId;
	}

	public String getPlatformServiceId() {
		return platformServiceId;
	}

	public void setPlatformServiceId(String platformServiceId) {
		this.platformServiceId = platformServiceId;
	}

	public String getChatServiceId() {
		return chatServiceId;
	}

	public void setChatServiceId(String chatServiceId) {
		this.chatServiceId = chatServiceId;
	}

	@Override
	public String toString() {
		return "GameUser [userId=" + userId + ", gatewayServiceId=" + gatewayServiceId + ", platformServiceId="
				+ platformServiceId + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GameUser) {
			return this.getUserId() == ((GameUser) obj).getUserId();
		}
		return false;

	}

	@Override
	public String getKeyName() {
		return CachePrefixConsts.CACHE_KEY_GAMEUSER + userId;
	}

}
