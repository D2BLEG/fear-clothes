package com.fearclothes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FearClothesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FearClothesApplication.class, args);
    }
}
