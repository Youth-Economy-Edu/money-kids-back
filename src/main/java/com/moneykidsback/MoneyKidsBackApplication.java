package com.moneykidsback; // 본인의 패키지 경로

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

// MoneyKidsBack 애플리케이션의 메인 클래스
@SpringBootApplication
@EnableScheduling
public class MoneyKidsBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyKidsBackApplication.class, args);
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}