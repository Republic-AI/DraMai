package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import lombok.*;

import java.util.List;
import java.util.Map;

//python发过来的npc行为
public class NpcActionRequest extends BaseMsg<Map<String, Object>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_ACTION_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_ACTION_COMMAND;
    }

    //NPC ID，非空
    @Getter
    @Setter
    private Long npcId;

    //Action ID，非空
    @Getter
    @Setter
    private int actionId;

    @Setter
    @Getter
    private Long ack;

    @Setter
    @Getter
    private List<String> speak;

    @Setter
    @Getter
    private long durationTime;

    @Setter
    @Getter
    private String mood;

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
        //地图对象ID
        public String oid;
        //"卖家NPC ID
        public Integer npcId;
    }

    //购买行为编号：103
    @Data
    public static class BuyData {
        //地图对象ID
        public String oid;
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
        //销售价格
        public Long price;
        //"卖家NPC ID
        public Integer npcId;
    }

    //做饭行为编号：104
    @Data
    public static class CookData {
        //做饭区域编号, 厨房灶台位置或者坐标（X,Y)
        public String oid;
        public List<Item> items;
    }

    public static class Item{
        //物品ID，对应道具表：Item.xls表中的ID
        public Integer itemId;
        //销售物品数量
        public Integer count;
    }


    //吃饭行为编号：105
    @Data
    public static class DinningData {
        //吃饭的地方：地图上桌子ID
        public String oid;
    }

    //睡眠行为编号：106
    @Data
    public static class SleepData {
        //地图上物品的ID：床的ID
        public String oid;
        //起床时间，睡觉的时候就决定了
        public String getUpTime;
    }

    //起床行为编号：110
    @Data
    public static class GetUpData {
        //地图上物品的ID：床的ID
        public String oid;
    }

    //说话行为编号：111
    @Data
    public static class SpeakData {
        //说话的具体内容
        public String content;
        //聊天对象ID
        public Long npcId;
    }

    //投喂行为编号：107
    @Data
    public static class FeedingData {
        //地图上牛这个对象的ID
        public String oid;
        //消耗的物品
        public List<Item> items;
    }

    //屠宰行为编号：108
    @Data
    public static class SlaughterData {
        //地图上牛这个对象的ID，要屠宰的牛对象ID
        public String oid;
    }

    //制作行为编号：109
    @Data
    public static class MakeData {
        //地图上厨房桌子的ID
        public String oid;
        //消耗的物品
        public List<Item> items;
    }

    //移动行为编号：112
    @Data
    public static class MoveData {
        //目标地：物品ID
        private String oid;

        private Integer gridX;

        private Integer gridY;

        private Long npcId;

    }

    //移动行为编号：113
    @Data
    public static class TypeData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class ThinkData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class RepairData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class ReadData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class ReplyChatData {
        //回复的内容
        private String content;
        //回复的chatData
        private ChatData chatData;
    }

    //说话行为编号：118
    @Data
    public static class NpcTalkData {
        //说话的具体内容
        public String content;
        //聊天对象ID
        public Long npcId;
        //是否是发起者
        public boolean initiator;
        //谈话地点
        public int gridX;
        public int girdY;

        public int endingTalk;
    }

    @Data
    public static class FishingData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class TidyUpData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class CommonData {
        //目标地：物品ID
        private String oid;
    }

    @Data
    public static class ShareData {
        //目标地：物品ID
        public Long npcId;
    }
}
