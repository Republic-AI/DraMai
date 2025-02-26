package com.infinity.ai.telegram.common;

import com.infinity.common.utils.ConvertUtil;
import com.infinity.common.utils.DesUtils;

public class SecretUtitls {

    //加密
    public static String encrypt(String value) {
        if (value == null || value.trim().length() == 0) {
            return "";
        }

        try {
            final byte[] encryptToken = DesUtils.encrypt(value.getBytes("utf-8"), DesUtils.getTokenKey());
            return ConvertUtil.getHexStr(encryptToken, false);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //解密
    public static String decrypt(String value) {
        if (value == null || value.trim().length() == 0) {
            return "";
        }
        try {
            byte[] hexValue = ConvertUtil.getHexValue(value);
            byte[] decrypt = DesUtils.decrypt(hexValue, DesUtils.getTokenKey());
            return new String(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(encrypt("TG1114825302"));
        System.out.println(encrypt("TG2224825302"));
    }
}
