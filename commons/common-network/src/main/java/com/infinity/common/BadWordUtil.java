package com.infinity.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BadWordUtil {

    /**
     * 最小匹配规则
     */
    private int minMatchTYpe = 1;

    /**
     * 最大匹配规则
     */
    private int maxMatchType = 2;

    private static class BadWordUtilHolder {
        private static final BadWordUtil INSTANCE = new BadWordUtil();
    }

    public static BadWordUtil getInstance() {
        return BadWordUtilHolder.INSTANCE;
    }


    /***
     * 敏感词检测调用接口
     * @param content 检测内容
     * @return k1: true:通过，false:不通过
     */
    public boolean check(String content) {
        content = content.replaceAll("[\\pP\\p{Punct}]", "");
        Set<String> set = getBadWord(content, minMatchTYpe, true);
        return set == null || set.size() == 0;
    }

    public void init(Map wordsMap) {

        this.wordsMap = wordsMap;
    }

    private Map wordsMap;

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return 如果存在，则返回敏感词字符的长度，不存在返回0
     */
    private int checkBadWord(String txt, int beginIndex, int matchType) {
        // 敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        // 匹配标识数默认为0
        int matchFlag = 0;
        char word = 0;
        Map nowMap = this.wordsMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            // 获取指定key
            nowMap = (Map) nowMap.get(word);
            // 存在，则判断是否为最后一个
            if (nowMap != null) {
                // 找到相应key，匹配标识+1
                matchFlag++;
                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    // 结束标志位为true
                    flag = true;
                    // 最小规则，直接返回,最大规则还需继续查找
                    if (minMatchTYpe == matchType) {
                        break;
                    }
                }
            } else {
                // 不存在，直接返回
                break;
            }
        }

        if (!flag) {
            matchFlag = 0;
        }

        return matchFlag;
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt       文字
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     * @return
     */
    private Set<String> getBadWord(String txt, int matchType, boolean asap) {
        Set<String> sensitiveWordList = new HashSet<String>();
        for (int i = 0; i < txt.length(); i++) {
            // 判断是否包含敏感字符
            int length = checkBadWord(txt, i, matchType);
            // 存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                if (asap) break;
                // 减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    /**
     * 替换敏感字符
     */
    public String replaceSensitiveWord(String content, String replaceChar) {
        content = content.replaceAll("[\\pP\\p{Punct}]", "");
        String resultTxt = content;
        // 获取所有的敏感词
        Set<String> set = getBadWord(content, minMatchTYpe, true);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }
        return resultTxt;
    }


    /**
     * 获取替换字符串
     */
    private String getReplaceChars(String replaceChar, int length) {
        return replaceChar + replaceChar.repeat(Math.max(0, length - 1));
    }
}