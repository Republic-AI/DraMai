package com.infinity.common.base.data;

import com.infinity.common.cache.CacheObj;
import com.infinity.common.cache.lv2.ObjectCacheRedisService;
import com.infinity.common.consts.CachePrefixConsts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 相当于本地缓存 涉及到 缓存同步问题<br>
 * 暂定策略：本地缓存一份，redis一份，无论哪个节点发生gameuser对象改变，都发送广播通知其他节点去redis重新读一份
 * 修改和删除的时候需要发送广播通知
 */
public class GameUserMgr {
    private static ConcurrentHashMap<Long, GameUser> gameUsers = new ConcurrentHashMap();

    public static void refreshGameUser(long userId) {
        GameUser gameUser = ObjectCacheRedisService.get(CacheObj.makeKeyName(CachePrefixConsts.CACHE_KEY_GAMEUSER + userId),
                GameUser.class);
        gameUsers.put(Long.valueOf(gameUser.getUserId()), gameUser);
    }

    public static GameUser getGameUser(Long userId) {
        GameUser gameUser = gameUsers.get(Long.valueOf(userId));
        if (gameUser == null) {
            // 去redis读一份 放本地
            gameUser = ObjectCacheRedisService.get(CacheObj.makeKeyName(CachePrefixConsts.CACHE_KEY_GAMEUSER + userId),
                    GameUser.class);

            //todo FG
            if (gameUser != null) {
                gameUsers.put(Long.valueOf(gameUser.getUserId()), gameUser);
            }
        }
        return gameUser;
    }

    public static void removeGameUser(long userId, RefreshPlayerHandler handler) {
        if (handler != null) {
            handler.refreshGameUser();
        }
        gameUsers.remove(Long.valueOf(userId));
        //延迟10秒删除
        String redisKey = CacheObj.makeKeyName(CachePrefixConsts.CACHE_KEY_GAMEUSER + userId);
        ObjectCacheRedisService.expire(redisKey, 30);
    }

    public static void addGameUser(GameUser gameUser, RefreshPlayerHandler handler) {
        ObjectCacheRedisService.saveObject(CacheObj.makeKeyName(CachePrefixConsts.CACHE_KEY_GAMEUSER + gameUser.getUserId()),
                gameUser);
        gameUsers.put(Long.valueOf(gameUser.getUserId()), gameUser);
        if (handler != null) {
            handler.refreshGameUser();
        }
    }

    public static List<Long> getRandomKeys(int n) {
        List<Long> reservoir = new ArrayList<>(n);
        Iterator<Long> iterator = gameUsers.keySet().iterator();
        SplittableRandom random = new SplittableRandom();

        int index = 0;
        // 首先将前n个元素直接放入水塘
        while (iterator.hasNext() && index < n) {
            reservoir.add(iterator.next());
            index++;
        }

        // 对后续元素进行随机替换
        while (iterator.hasNext()) {
            Long key = iterator.next();
            int randomIndex = random.nextInt(++index);
            if (randomIndex < n) {
                reservoir.set(randomIndex, key);
            }
        }

        return reservoir;
    }

    public static GameUser getGameUser() {
        for (GameUser gameUser : gameUsers.values()) {
            return gameUser;
        }
        return null;
    }

    public static GameUser getGameUser(String sender) {
        for (GameUser gameUser : gameUsers.values()) {
            if (gameUser.getUserNo().equals(sender)) {
                return gameUser;
            }
        }
        return null;
    }
}
