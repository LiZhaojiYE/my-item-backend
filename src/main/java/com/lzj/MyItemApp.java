package com.lzj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @author 李兆基 */
@SpringBootApplication
@MapperScan("com.lzj.mapper")
public class MyItemApp {
    public static void main(String[] args) {
        SpringApplication.run(MyItemApp.class, args);
    }
}

