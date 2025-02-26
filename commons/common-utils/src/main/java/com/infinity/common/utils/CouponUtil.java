package com.infinity.common.utils;

public class CouponUtil {
    private static final Long COUPON_ID_BIT_LEN = 15L;
    private static final Long NUMBER_OF_ONE_BIT_LEN = 19L;
    private static final Long REMAINDER_BIT_LEN = 7L;
    private static final long DIVISOR = 1L << REMAINDER_BIT_LEN;
    private static final char[] r =
            new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
                    'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                    'X', 'Z', 'Y', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    private static final int l = r.length;

    private CouponUtil() {
        throw new UnsupportedOperationException("can not be construct to a instance");
    }

    /**
     * 生成兑换码编码
     *
     * @param couponSchemeId  活动Id
     * @param redeemSerialNum 优惠券编号
     * @return
     */
    public static long enRedeemNum(long couponSchemeId, long redeemSerialNum) {
        // COUPON_ID_BIT_LEN表示活动Id二进制位数，这里是15
        redeemSerialNum = redeemSerialNum << COUPON_ID_BIT_LEN;
        // 使用或运算将活动id和优惠券编号组合在一起
        long r = couponSchemeId | redeemSerialNum;
        // 下面整合校验码到编码中
        long n = numOfOne(r);
        long re = r % DIVISOR;
        r = (r << NUMBER_OF_ONE_BIT_LEN) | n;
        r = (r << REMAINDER_BIT_LEN) | re;
        return r;
    }

    /**
     * 判断二进制中1的位数
     *
     * @param n
     * @return
     */
    private static long numOfOne(long n) {
        long count = 0;
        long seed = n;
        while (seed != 0) {
            seed = seed & (seed - 1);
            count++;
        }
        return count;
    }

    //生成无规则字符串
    public static String enRedeemCode(long redeemNum) {
        char[] buf = new char[32];
        int charPos = 32;
        while ((redeemNum / l) > 0) {
            int ind = (int) (redeemNum % l);
            buf[--charPos] = r[ind];
            redeemNum /= l;
        }
        buf[--charPos] = r[(int) (redeemNum % l)];
        String str = new String(buf, charPos, (32 - charPos));
        return str;
    }

    public static String getCode(long couponSchemeId, long redeemSerialNum) {
        return enRedeemCode(enRedeemNum(couponSchemeId, redeemSerialNum));
    }
}
