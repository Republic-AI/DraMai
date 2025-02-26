package com.infinity.ai.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.infinity.cat", "com.infinity.common"})
@SpringBootApplication
public class QuartzService {
    public static void main(String[] args) {
        SpringApplication.run(QuartzService.class, args);
    }
}