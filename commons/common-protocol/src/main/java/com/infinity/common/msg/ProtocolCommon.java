package com.infinity.common.msg;

public class ProtocolCommon {
    /**************** type *****************/
    public static final int MSG_TYPE_PLATFORM = 1;
    public static final int MSG_TYPE_CHAT = 2;
    public static final int MSG_TYPE_PYTHON = 3;
    public static final int MSG_TYPE_TIMER = 8;
    public static final int MSG_TYPE_CLUSTER = 9;

    public static final int MSG_DISPATCH = 1000;
    public static final int MSG_RESPONSE = 1001;
    public static final int MSG_BROADCAST = 1002;
    public static final int MSG_ERROR = 4001;

    /**************** PLATFORM ****************/
    public static final int OFF_LINE_COMMAND = 99999;
    public static final int KICK_LINE_COMMAND = 99998;
    public static final int SYS_REFRESH_COMMAND = 99997;
    public static final int SYS_HEART_COMMAND = 99996;

    public static final int LOGIN_COMMAND = 10000;
    public static final int CHARATER_SET_COMMAND = 10001;
    public static final int CHARATER_QUERY_COMMAND = 10002;
    public static final int NEW_NPC_NOTIFY = 10004;
    public static final int NPC_OFFLINE_NOTIFY = 10005;
    public static final int FRAME_SYNC_COMMAND = 10006;
    public static final int NPC_ACTION_BROAD_COMMAND = 10007;
    public static final int SYNC_NPC_ACTION_COMMAND = 10008;
    public static final int QUERY_MAP_DATA_COMMAND = 10009;
    public static final int QUARTZ_FARMING_COMMAND = 10010;
    public static final int MAP_DATA_BROAD_COMMAND = 10011;

    public static final int SWITH_LIVE_COMMAND = 10012;
    public static final int QUERY_GIFT_COMMAND = 10013;
    public static final int SEND_GIFT_COMMAND = 10014;
    public static final int QUERY_RANK_COMMAND = 10015;


    public static final int kChatCommand = 10016;
    public static final int kQueryChatCommand = 10017;
    public static final int kChatNotifyCommand = 10018;

    public static final int kSignCommand = 10019;
    public static final int kGmCommand = 10023;

    public static final int PLAYER_MOVE_COMMAND = 10024;
    public static final int CHAT_HISTORY_COMMAND = 10025;
    public static final int PLAYER_SPEAK_COMMAND = 10026;

    public static final int PLAYER_NFT_COMMAND = 10027;
    public static final int QUERY_NFT_COMMAND = 10028;

    public static final int NPC_COMMAND_SYNC_COMMAND = 10029;
    public static final int NPC_ACTION_COMMAND = 10100;
    public static final int NPC_DATA_SYNC_COMMAND = 10101;
    public static final int QUERY_NPC_DATA_COMMAND = 10102;
    public static final int NPC_REPLY_CHAT_COMMAND = 10103;
    //绑定twitter
    public static final int TWITTER_CODE_COMMAND = 10104;
    //Type增加道具
    public static final int TYPE_TO_EARN_COMMAND = 10105;
    //NPC对话
    public static final int NPC_TALK_COMMAND = 10106;
    public static final int P_NPC_TALK_COMMAND = 10107;
    //NPC换装
    public static final int NPC_CHANGE_COMMAND = 10108;
    //拉取房间列表
    public static final int QUERY_ROOM_LIST_COMMAND = 10109;
    //离开房间
    public static final int LEAVE_LIVE_COMMAND = 10110;
    //修改房间家具
    public static final int ROOM_CHANGE_COMMAND = 10111;
    //拉取tweets
    public static final int QUERY_TWEETS_COMMAND = 10112;
    //点赞或评论
    public static final int COMMENT_TWEET_COMMAND = 10113;
    //拉取聊天记录
    public static final int QUERY_PLAYER_NPC_CHAT_COMMAND = 10114;
    //NCP指令
    public static final int NPC_COMMAND_COMMAND = 10115;
    //查询投票信息
    public static final int QUERY_VOTE_COMMAND = 10116;
    //投票
    public static final int OP_VOTE_COMMAND = 10117;
    //投票通知
    public static final int VOTE_NOTIFY_COMMAND = 10118;
    //投票历史
    public static final int VOTE_HISTORY_COMMAND = 10119;

    public static final int QUARTZ_GETUP_COMMAND = 10013;


    public static final int kGoodsSelectCommand = 100081;
    public static final int kNotifyGoodsCommand = 100091;
    public static final int kTaskChangeNotifyCommand = 100101;
    public static final int kTaskRestNotifyCommand = 100111;
    public static final int kQueryTaskCommand = 100121;
    public static final int kTaskRewardReceiveCommand = 100131;

    /**************** platform end *************/

    /**************** 定时任务 ******************/
    public static final int MSG_CODE_TIMER_EVERYDAYZERO = 80002;
    public static final int MSG_CODE_TIMER_SUBMIT = 80003;
    /**************** 定时任务end ****************/

    /**************** 集群和分布式消息 ************/
    public static final int MSG_CODE_REFRESH_GAMEUSER = 90003;
    /**************** 集群和分布式消息 end ********/

}
