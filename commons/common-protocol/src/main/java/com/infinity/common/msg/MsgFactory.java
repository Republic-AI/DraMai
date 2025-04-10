package com.infinity.common.msg;


import com.infinity.common.msg.chat.*;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;
import com.infinity.common.msg.common.RefreshMsg;
import com.infinity.common.msg.common.ResponseOk;
import com.infinity.common.msg.platform.chat.PChatNpcRequest;
import com.infinity.common.msg.platform.chat.QueryNpcPlayerRequest;
import com.infinity.common.msg.platform.chat.QueryNpcPlayerResponse;
import com.infinity.common.msg.platform.gm.GmRequest;
import com.infinity.common.msg.platform.gm.GmResponse;
import com.infinity.common.msg.platform.goods.*;
import com.infinity.common.msg.platform.live.*;
import com.infinity.common.msg.platform.map.QueryMapDataRequest;
import com.infinity.common.msg.platform.map.QueryMapDataResponse;
import com.infinity.common.msg.platform.npc.*;
import com.infinity.common.msg.platform.player.*;
import com.infinity.common.msg.platform.room.QueryRoomListReponse;
import com.infinity.common.msg.platform.room.QueryRoomListRequest;
import com.infinity.common.msg.platform.room.RoomChangeReponse;
import com.infinity.common.msg.platform.room.RoomChangeRequest;
import com.infinity.common.msg.platform.sys.BroadcastRequest;
import com.infinity.common.msg.platform.sys.HeartRequest;
import com.infinity.common.msg.platform.task.*;
import com.infinity.common.msg.platform.tweet.OperateTweetReponse;
import com.infinity.common.msg.platform.tweet.OperateTweetRequest;
import com.infinity.common.msg.platform.tweet.QueryTweetReponse;
import com.infinity.common.msg.platform.tweet.QueryTweetRequest;
import com.infinity.common.msg.platform.vote.*;
import com.infinity.common.msg.timer.EveryDayZeroMessage;
import com.infinity.common.msg.timer.SubmitExpridedMessage;

import java.util.HashMap;
import java.util.Map;

public class MsgFactory {

    private static Map<String, Class<? extends BaseMsg>> dataMap = new HashMap<>();

    static {
        dataMap.put(MsgType.Request.value + LoginRequest.getCmd(), LoginRequest.class);
        dataMap.put(MsgType.Response.value + LoginResponse.getCmd(), LoginResponse.class);
        dataMap.put(MsgType.Request.value + CharaterSetRequest.getCmd(), CharaterSetRequest.class);
        dataMap.put(MsgType.Response.value + CharaterSetResponse.getCmd(), CharaterSetResponse.class);
        dataMap.put(MsgType.Request.value + QueryCharaterRequest.getCmd(), QueryCharaterRequest.class);
        dataMap.put(MsgType.Response.value + QueryCharaterResponse.getCmd(), QueryCharaterResponse.class);
        dataMap.put(MsgType.Response.value + BroadcastRequest.getCmd(), BroadcastRequest.class);
        dataMap.put(MsgType.Request.value + FrameSyncRequest.getCmd(), FrameSyncRequest.class);
        dataMap.put(MsgType.Request.value + NpcActionRequest.getCmd(), NpcActionRequest.class);
        dataMap.put(MsgType.Response.value + NpcActionResponse.getCmd(), NpcActionResponse.class);
        dataMap.put(MsgType.Request.value + QueryNpcDataRequest.getCmd(), QueryNpcDataRequest.class);
        dataMap.put(MsgType.Response.value + QueryNpcDataResponse.getCmd(), QueryNpcDataResponse.class);
        dataMap.put(MsgType.Request.value + SyncNpcActionRequest.getCmd(), SyncNpcActionRequest.class);
        dataMap.put(MsgType.Request.value + QueryMapDataRequest.getCmd(), QueryMapDataRequest.class);
        dataMap.put(MsgType.Response.value + QueryMapDataResponse.getCmd(), QueryMapDataResponse.class);
        dataMap.put(MsgType.Request.value + NpcActionBroadRequest.getCmd(), NpcActionBroadRequest.class);
        dataMap.put(MsgType.Response.value + NpcActionBroadRequest.getCmd(), NpcActionBroadRequest.class);
        dataMap.put(MsgType.Request.value + NpcDataSyncRequest.getCmd(), NpcDataSyncRequest.class);

        dataMap.put(MsgType.Request.value + QueryGiftRequest.getCmd(), QueryGiftRequest.class);
        dataMap.put(MsgType.Response.value + QueryGiftResponse.getCmd(), QueryGiftResponse.class);
        dataMap.put(MsgType.Request.value + QueryRankRequest.getCmd(), QueryRankRequest.class);
        dataMap.put(MsgType.Response.value + QueryRankResponse.getCmd(), QueryRankResponse.class);
        dataMap.put(MsgType.Request.value + SendGiftRequest.getCmd(), SendGiftRequest.class);
        dataMap.put(MsgType.Response.value + SendGiftResponse.getCmd(), SendGiftResponse.class);
        dataMap.put(MsgType.Request.value + SwithLiveRequest.getCmd(), SwithLiveRequest.class);
        dataMap.put(MsgType.Response.value + SwithLiveResponse.getCmd(), SwithLiveResponse.class);
        dataMap.put(MsgType.Request.value + TypeToEarnRequest.getCmd(), TypeToEarnRequest.class);
        dataMap.put(MsgType.Response.value + TypeToEarnReponse.getCmd(), TypeToEarnReponse.class);

        dataMap.put(MsgType.Request.value + ChatRequest.getCmd(), ChatRequest.class);
        dataMap.put(MsgType.Response.value + ChatResponse.getCmd(), ChatResponse.class);
        dataMap.put(MsgType.Request.value + QueryChatRequest.getCmd(), QueryChatRequest.class);
        dataMap.put(MsgType.Response.value + QueryChatResponse.getCmd(), QueryChatResponse.class);


        dataMap.put(MsgType.Request.value + QueryGoodsRequest.getCmd(), QueryGoodsRequest.class);
        dataMap.put(MsgType.Response.value + QueryGoodsResponse.getCmd(), QueryGoodsResponse.class);

        dataMap.put(MsgType.Response.value + NotifyGoodsRequest.getCmd(), NotifyGoodsRequest.class);
        dataMap.put(MsgType.Response.value + NotifyTaskRequest.getCmd(), NotifyTaskRequest.class);
        dataMap.put(MsgType.Response.value + NotifyTaskResetRequest.getCmd(), NotifyTaskResetRequest.class);


        dataMap.put(MsgType.Request.value + RefreshPlayerMessage.getCmd(), RefreshPlayerMessage.class);
        dataMap.put(MsgType.Response.value + RefreshPlayerMessage.getCmd(), RefreshPlayerMessage.class);

        dataMap.put(MsgType.Request.value + QueryTaskRequest.getCmd(), QueryTaskRequest.class);
        dataMap.put(MsgType.Response.value + QueryTaskResponse.getCmd(), QueryTaskResponse.class);

        dataMap.put(MsgType.Request.value + TaskRewardReceiveRequest.getCmd(), TaskRewardReceiveRequest.class);
        dataMap.put(MsgType.Response.value + TaskRewardReceiveResponse.getCmd(), TaskRewardReceiveResponse.class);
        dataMap.put(MsgType.Request.value + SignRequest.getCmd(), SignRequest.class);
        dataMap.put(MsgType.Response.value + SignResponse.getCmd(), SignResponse.class);
        dataMap.put(MsgType.Request.value + LogoutRequest.getCmd(), LogoutRequest.class);
        dataMap.put(MsgType.Response.value + LogoutResponse.getCmd(), LogoutResponse.class);
        dataMap.put(MsgType.Request.value + EveryDayZeroMessage.getCmd(), EveryDayZeroMessage.class);
        dataMap.put(MsgType.Response.value + EveryDayZeroMessage.getCmd(), EveryDayZeroMessage.class);

        dataMap.put(MsgType.Response.value + ResponseOk.getCmd(), ResponseOk.class);
        dataMap.put(MsgType.Request.value + GmRequest.getCmd(), GmRequest.class);
        dataMap.put(MsgType.Response.value + GmResponse.getCmd(), GmResponse.class);
        dataMap.put(MsgType.Request.value + SubmitExpridedMessage.getCmd(), SubmitExpridedMessage.class);
        dataMap.put(MsgType.Request.value + RefreshMsg.getCmd(), RefreshMsg.class);
        dataMap.put(MsgType.Response.value + RefreshMsg.getCmd(), RefreshMsg.class);
        dataMap.put(MsgType.Request.value + HeartRequest.getCmd(), HeartRequest.class);

        dataMap.put(MsgType.Request.value + NpcChatHistoryResponse.getCmd(), NpcChatHistoryRequest.class);
        dataMap.put(MsgType.Response.value + NpcChatHistoryRequest.getCmd(), NpcChatHistoryResponse.class);

        dataMap.put(MsgType.Request.value + PlayerMoveRequest.getCmd(), PlayerMoveRequest.class);

        dataMap.put(MsgType.Request.value + PlayerSpeakRequest.getCmd(), PlayerSpeakRequest.class);


        dataMap.put(MsgType.Request.value + PlayerNFTRequest.getCmd(), PlayerNFTRequest.class);
        dataMap.put(MsgType.Response.value + PlayerNFTResponse.getCmd(), PlayerNFTResponse.class);
        dataMap.put(MsgType.Request.value + QueryNFTRequest.getCmd(), QueryNFTRequest.class);
        dataMap.put(MsgType.Response.value + QueryNFTResponse.getCmd(), QueryNFTResponse.class);

        dataMap.put(MsgType.Request.value + ChatNpcRequest.getCmd(), ChatNpcRequest.class);
        dataMap.put(MsgType.Response.value + ChatNpcResponse.getCmd(), ChatNpcResponse.class);

        dataMap.put(MsgType.Request.value + PChatNpcRequest.getCmd(), PChatNpcRequest.class);

        dataMap.put(MsgType.Request.value + NpcChangeRequest.getCmd(), NpcChangeRequest.class);
        dataMap.put(MsgType.Response.value + NpcChangeResponse.getCmd(), NpcChangeResponse.class);

        dataMap.put(MsgType.Request.value + QueryRoomListRequest.getCmd(), QueryRoomListRequest.class);
        dataMap.put(MsgType.Response.value + QueryRoomListReponse.getCmd(), QueryRoomListReponse.class);

        dataMap.put(MsgType.Request.value + LeaveLiveRequest.getCmd(), LeaveLiveRequest.class);
        dataMap.put(MsgType.Response.value + LeaveLiveResponse.getCmd(), LeaveLiveResponse.class);

        dataMap.put(MsgType.Request.value + RoomChangeRequest.getCmd(), RoomChangeRequest.class);
        dataMap.put(MsgType.Response.value + RoomChangeReponse.getCmd(), RoomChangeReponse.class);

        dataMap.put(MsgType.Request.value + QueryTweetRequest.getCmd(), QueryTweetRequest.class);
        dataMap.put(MsgType.Response.value + QueryTweetReponse.getCmd(), QueryTweetReponse.class);

        dataMap.put(MsgType.Request.value + OperateTweetRequest.getCmd(), OperateTweetRequest.class);
        dataMap.put(MsgType.Response.value + OperateTweetReponse.getCmd(), OperateTweetReponse.class);

        dataMap.put(MsgType.Request.value + QueryNpcPlayerRequest.getCmd(), QueryNpcPlayerRequest.class);
        dataMap.put(MsgType.Response.value + QueryNpcPlayerResponse.getCmd(), QueryNpcPlayerResponse.class);

        dataMap.put(MsgType.Request.value + NpcCommandRequest.getCmd(), NpcCommandRequest.class);
        dataMap.put(MsgType.Response.value + NpcCommandResponse.getCmd(), NpcCommandResponse.class);

        dataMap.put(MsgType.Request.value + QueryVoteRequest.getCmd(), QueryVoteRequest.class);
        dataMap.put(MsgType.Response.value + QueryVoteResponse.getCmd(), QueryVoteResponse.class);

        dataMap.put(MsgType.Request.value + OpVoteRequest.getCmd(), OpVoteRequest.class);
        dataMap.put(MsgType.Response.value + OpVoteResponse.getCmd(), OpVoteResponse.class);

        dataMap.put(MsgType.Response.value + NotifyVoteResponse.getCmd(), NotifyVoteResponse.class);

        dataMap.put(MsgType.Request.value + VoteHistoryRequest.getCmd(), VoteHistoryRequest.class);
        dataMap.put(MsgType.Response.value + VoteHistoryResponse.getCmd(), VoteHistoryResponse.class);

    }

    public static Class<? extends BaseMsg> getMsgClass(final MsgType type, final int command) {
        return dataMap.get(type.value + command);
    }

    enum MsgType {
        Request("Rq"),
        Response("Rs");

        public String value;

        MsgType(String value) {
            this.value = value;
        }
    }
}
