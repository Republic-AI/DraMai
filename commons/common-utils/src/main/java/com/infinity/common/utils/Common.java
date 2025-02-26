package com.infinity.common.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Common {
    private static class CommonHolder {
        private final static Common kInstance_ = new Common();
    }


    public static Common getInstance() {
        return CommonHolder.kInstance_;
    }

    private Common() {
    }

    public static int RandomRangeInt(final int min, final int max) {

        return ThreadLocalRandom.current().nextInt(max) % (max - min + 1) + min;
    }

    public static int RandomRangeInt(final int min, final int max, Random random) {
        if (max <= 0) {
            return 0;
        }
        return random.nextInt(max) % (max - min + 1) + min;
    }

    private byte[] token_key_;

    public void setTokenKey(final byte[] tokenKey) {
        token_key_ = tokenKey;
    }

    public byte[] getTokenKey() {
        if (token_key_ != null)
            return token_key_;

        try {
            setTokenKey(DesUtils.initKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token_key_;
    }

    // 存入关卡的索引算法是章节是前16位，后16位是关卡, 0xFFFFFFFF & (((chapterId << 16) & 0xFFFF0000) + dunId)
    public final int makeDungeonIndex(final int chapterID, final int dungeonID) {
        return ((chapterID << 16) & 0xffff0000) + dungeonID;
    }

    public final Pair<Integer, Integer> parseDungeonIndex(final int dungeonIndex) {
        final int chapterID = (dungeonIndex & 0xffff0000) >> 16;
        final int dungeonID = dungeonIndex & 0x0000ffff;
        return new Pair<Integer, Integer>(chapterID, dungeonID);
    }

    public final boolean checkDungeonIndexAvailability(final int chapterID, final int dungeonID, final int dungeonIndex) {
        return makeDungeonIndex(chapterID, dungeonID) == dungeonIndex;
    }
}
