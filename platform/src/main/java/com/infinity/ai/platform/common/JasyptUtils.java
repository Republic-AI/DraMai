package com.infinity.ai.platform.common;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

public class JasyptUtils {
    private static String pwd = "33A7F!36D92$11B0&G52";
    private static String Algorithm = "PBEWithMD5AndDES";

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return
     */
    public static String encrypt(String plaintext) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        // 指定算法
        config.setAlgorithm(Algorithm);
        // 指定秘钥，和yml配置文件中保持一致
        config.setPassword(pwd);
        encryptor.setConfig(config);
        // 生成加密数据
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密
     *
     * @param data 加密后数据
     * @return
     */
    public static String decrypt(String data) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(Algorithm);
        config.setPassword(pwd);
        encryptor.setConfig(config);
        // 解密数据
        return encryptor.decrypt(data);
    }
}