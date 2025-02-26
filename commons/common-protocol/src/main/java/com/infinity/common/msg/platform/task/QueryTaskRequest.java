package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//查询玩家任务信息
public class QueryTaskRequest extends BaseMsg<QueryTaskRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kQueryTaskCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kQueryTaskCommand;
    }

    @Data
    public static class RequestData {
        //玩家ID
        private Long playerId;

        // 任务类型:-1:全部, 1:日常任务,2:成就任务
        private Integer type;

        // 任务子类型:日常任务（0主线、1日常、2周常、3支线),成就任务(0战斗、1伙伴、2装备进阶、3宠物、4竞技场、5工会),竞技(日常、周常),默认-1查询所有
        //private Integer subType;
    }
}
