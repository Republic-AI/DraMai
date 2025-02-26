package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.Map;

//客户端发送NPC行为结果数据给服务端
public class SyncNpcActionRequest extends BaseMsg<SyncNpcActionRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.SYNC_NPC_ACTION_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.SYNC_NPC_ACTION_COMMAND;
    }

    @Data
    public static class RequestData {
        //NPC ID，非空
        private Long npcId;
        //Action ID，非空
        //private int actionId;
        private Long bid;
        //行为是否完成：0：否，1：完成
        private int isFinish;

        //npc的坐标
        private Integer x;
        private Integer y;

        //地图对象ID
        private String objId;
        //地图对象状态
        private Integer state;

        //行为数据，可空
        private Map<String, Object> params;
    }

    //耕种行为编号：100
    @Data
    public static class FarmingData {
        public String oid;     //耕种区域编号,
        public Integer itemId;  //: 10101001,物品ID，对应道具表：Item.xls表中的ID"
    }

    //采收行为编号：101
    @Data
    public static class HarvestData {
        //采收区域,到哪个地方采收，若目前只有一个区域，则不填；要是有多个区域，则需要指定区域编号
        public String oid;
    }

    //采收行为编号：102
    @Data
    public static class SaleData {
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
        //销售价格
        public Double price;
        //"卖家NPC ID
        public Integer sellerNpcId;
    }

    //采收行为编号：103
    @Data
    public static class BuyData {
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
        //销售价格
        public Double price;
        //"卖家NPC ID
        public Integer sellerNpcId;
    }

    //做饭行为编号：104
    @Data
    public static class CookData {
        //做饭区域编号, 厨房灶台位置或者坐标（X,Y)
        public String oid;
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
    }

    //吃饭行为编号：105
    @Data
    public static class DinningData {
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
    }

    //睡眠行为编号：106
    @Data
    public static class SleepData {
        //地图上物品的ID：床的ID
        public String oid;
    }

    //起床行为编号：110
    @Data
    public static class GetUpData {

    }

    //说话行为编号：111
    @Data
    public static class SpeakData {
        //说话的具体内容
        public String content;
        //聊天对象ID
        public Integer npcId;
    }

    //投喂行为编号：107
    @Data
    public static class FeedingData {

    }

    //屠宰行为编号：108
    public static class SlaughterData {

    }

    //制作行为编号：109
    public static class MakeData {

    }

    //移动行为编号：112
    @Data
    public static class MoveData {
        //目标地：物品ID
        private String oid;
    }
}
