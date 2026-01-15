package com.example.booktrading;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 二手书交易管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.example.booktrading.mapper")
public class BookTradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTradingApplication.class, args);
    }

}
