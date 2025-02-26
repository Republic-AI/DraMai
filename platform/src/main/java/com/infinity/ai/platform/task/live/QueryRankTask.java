package com.infinity.ai.platform.task.live;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.service.IPlayerRepository;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.QueryRankRequest;
import com.infinity.common.msg.platform.live.QueryRankResponse;
import com.infinity.common.msg.platform.live.RankData;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 查询排行榜
 * from: H5
 * target: platform
 */
@Slf4j
public class QueryRankTask extends BaseTask<QueryRankRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_RANK_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryRankRequest msg = this.getMsg();
        if (playerId <= 0) {
            log.debug("QueryRankTask playerId is error,request msg={}", msg);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return true;
        }

        sendMessage(buildResponse(player));
        return true;
    }

    private BaseMsg buildResponse(Player player) {
        QueryRankRequest msg = this.getMsg();
        //组装数据
        QueryRankResponse response = new QueryRankResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(player.getPlayerID());
        QueryRankResponse.ResponseData data = new QueryRankResponse.ResponseData();

        int pageSize = 10;
        int pageIndex = 1;
        int offset = (pageIndex - 1) * pageSize;

        Long roomId = msg.getData().getRoomId();
        RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_RANK;
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RScoredSortedSet<Long> roomRank = redissonClient.getScoredSortedSet(liveRoom.getKey(roomId));

        //房间排行榜
        Collection<Long> playerIds = roomRank.valueRangeReversed(0, true, Double.MAX_VALUE, true, offset, pageSize);
        if (playerIds != null && playerIds.size() > 0) {
            List<Long> playerIdList = playerIds.stream().collect(Collectors.toList());
            List<Double> scoreList = roomRank.getScore(playerIdList);

            IPlayerRepository repository = SpringContextHolder.getBean(IPlayerRepository.class);
            List<RankData> result = new ArrayList<>();
            for (int i = 0; i < playerIdList.size(); i++) {
                Long playerId = playerIdList.get(i);
                Player user = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
                String userName = (user == null) ? repository.findNameById(playerId) : user.getName();
                result.add(RankData.builder().name(userName).score(scoreList.get(i).longValue()).sort(i + 1).build());
            }
        }

        //当前玩家排名
        Integer rank = roomRank.rank(playerId);
        if (rank != null && rank >= 0) {
            Double score = roomRank.getScore(playerId);
            RankData me = RankData.builder().name(player.getName()).score(score == null ? 0L : score.longValue()).sort(rank + 1).build();
            data.setMe(me);
        }

        response.setData(data);
        return response;
    }
}
