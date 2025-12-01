package com.corki.demo;

import com.corki.common.annotation.EnableScaApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableScaApplication
@MapperScan("com.corki.demo.dao.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
