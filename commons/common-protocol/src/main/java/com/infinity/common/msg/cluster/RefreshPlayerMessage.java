package com.infinity.common.msg.cluster;

import com.infinity.common.msg.ProtocolCommon;

public class RefreshPlayerMessage extends ClusterMessage {

	@Override
	public int getCommand() {
		return ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER;
	}

	public static int getCmd(){
		return ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER;
	}

	private String sourceServiceId;// 哪个服务节点改的
	private long userId;

	//刷新类型：0：更新，1：删除
	private int operate = 0;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getSourceServiceId() {
		return sourceServiceId;
	}

	public void setSourceServiceId(String sourceServiceId) {
		this.sourceServiceId = sourceServiceId;
	}

	public int getOperate() {
		return operate;
	}

	public void setOperate(int operate) {
		this.operate = operate;
	}
}
