package com.infinity.common.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TrieWordFilter {

    @Getter
    private static final TrieWordFilter instance = new TrieWordFilter();

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    private final TrieNode root = new TrieNode();

    // 添加屏蔽词
    public void addBlockedWord(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    public boolean checkBlockedWords(String text) {
        for (int i = 0; i < text.length(); i++) {
            TrieNode node = root;
            for (int j = i; j < text.length(); j++) {
                char ch = Character.toLowerCase(text.charAt(j));
                if (!node.children.containsKey(ch)) {
                    break;
                }
                node = node.children.get(ch);
                if (node.isEndOfWord) {
                    return true;
                }
            }
        }
       return false;
    }
    // 替换屏蔽词
    public String filterBlockedWords(String text) {
        StringBuilder result = new StringBuilder();
        TrieNode node;
        int start = 0;

        while (start < text.length()) {
            node = root;
            int pos = start;
            boolean found = false;

            while (pos < text.length() && node.children.containsKey(text.charAt(pos))) {
                node = node.children.get(text.charAt(pos));
                if (node.isEndOfWord) {
                    found = true;
                    break;
                }
                pos++;
            }

            if (found) {
                result.append("*".repeat(pos - start + 1));
                start = pos + 1;
            } else {
                result.append(text.charAt(start));
                start++;
            }
        }

        return result.toString();
    }

}
