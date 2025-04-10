package com.infinity.ai.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.infinity.ai","com.infinity.common"})
@SpringBootApplication
public class HallService {
    public static void main(String[] args) {
        SpringApplication.run(HallService.class, args);
    }
}
