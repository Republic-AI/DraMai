package com.infinity.common.msg.cluster;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;

public abstract class ClusterMessage extends BaseMsg {

	@Override
	public int getType() {
		return ProtocolCommon.MSG_TYPE_CLUSTER;
	}
}
