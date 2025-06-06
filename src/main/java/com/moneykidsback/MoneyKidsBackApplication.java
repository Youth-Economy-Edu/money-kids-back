package com.moneykidsback; // 본인의 패키지 경로

import org.springframework.boot.SpringApplication;
// 아래 2개의 import 문을 추가하세요.
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// @SpringBootApplication 어노테이션에 (exclude = ...) 부분을 추가합니다.
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MoneyKidsBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyKidsBackApplication.class, args);
    }

}