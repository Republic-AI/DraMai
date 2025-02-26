package com.infinity.ai.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.infinity.ai","com.infinity.common"})
@SpringBootApplication
public class ChatService {
    public static void main(String[] args) {
        SpringApplication.run(ChatService.class, args);
    }
}
