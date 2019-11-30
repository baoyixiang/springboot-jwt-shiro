package com.bao.shirojwt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.bao.shirojwt.dao")
@EnableCaching
public class ShirojwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShirojwtApplication.class, args);
    }

}
