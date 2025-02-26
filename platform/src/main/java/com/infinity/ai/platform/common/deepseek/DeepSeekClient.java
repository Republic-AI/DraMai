package com.infinity.ai.platform.common.deepseek;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DeepSeekClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public DeepSeekResponse callDeepSeekWithHeaders(String url, String input, String apiKey) {
        // 准备请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(input, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response.getBody(), DeepSeekResponse.class);
        } catch (JsonProcessingException e) {
            log.error("callDeepSeekWithHeaders erroe", e);
        }
        return null;
    }
}
