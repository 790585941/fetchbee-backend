package com.example.fetchbeebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.fetchbeebackend.mapper")
public class FetchbeeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FetchbeeBackendApplication.class, args);
    }

}
