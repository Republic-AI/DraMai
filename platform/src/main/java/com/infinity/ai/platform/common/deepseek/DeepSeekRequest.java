package com.infinity.ai.platform.common.deepseek;

import lombok.Data;

import java.util.List;

@Data
public class DeepSeekRequest {

    private String model;

    private List<Data> messages;

    private boolean stream;

    private int max_tokens;

    private double presence_penalty;

    @lombok.Data
    public static class Data {
        private String role;
        private String content;
    }
}
