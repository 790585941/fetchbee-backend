package com.example.fetchbeebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.fetchbeebackend.mapper")
@EnableScheduling
public class FetchbeeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FetchbeeBackendApplication.class, args);
    }

}
