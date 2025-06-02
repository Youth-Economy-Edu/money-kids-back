package com.moneykidsback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class MoneyKidsBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyKidsBackApplication.class, args);
    }

}
