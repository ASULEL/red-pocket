package com.asule.redpocket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.asule.redpocket.mapper")
@EnableAsync
public class RedPocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedPocketApplication.class, args);
    }
}
