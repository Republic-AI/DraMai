package com.infinity.ai.platform.web.common;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
public class SignUtil {

    /**
     * 生成签名
     *
     * @param appId     APPID
     * @param appSecret APP秘钥
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return
     */
    public static String getSignature(String appId, String appSecret, String timestamp, String nonce) {
        // 对token、timestamp和nonce按字典排序
        String[] paramArr = new String[]{appId, timestamp, nonce, appSecret};
        Arrays.sort(paramArr);

        // 将排序后的结果拼接成一个字符串
        String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]).concat(paramArr[3]);

        String ciphertext = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 对接后的字符串进行sha1加密
            byte[] digest = md.digest(content.getBytes());
            ciphertext = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("生成签名异常", e);
        }

        return ciphertext;
    }

    /**
     * 校验签名
     *
     * @param appSecret APP秘钥
     * @param signature APP客户端加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return
     */
    public static boolean checkSignature(String appSecret, String signature, String timestamp, String nonce) {
        if (signature == null || timestamp == null || nonce == null)
            return false;

        // 对token、timestamp和nonce按字典排序
        String[] paramArr = new String[]{timestamp, nonce, appSecret};
        Arrays.sort(paramArr);

        // 将排序后的结果拼接成一个字符串
        String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);//.concat(paramArr[3]);

        String ciphertext = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 对接后的字符串进行sha1加密
            byte[] digest = md.digest(content.getBytes());
            ciphertext = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("校验签名异常", e);
        }

        // 将sha1加密后的字符串与signature进行对比
        return ciphertext != null ? ciphertext.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
