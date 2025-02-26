package com.infinity.ai.platform;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableEncryptableProperties
@ComponentScan({"com.infinity.ai","com.infinity.common"})
@SpringBootApplication
public class PlatformService {
    public static void main(String[] args) {
        SpringApplication.run(PlatformService.class, args);
    }
}
