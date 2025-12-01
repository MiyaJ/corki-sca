package com.corki.order;

import com.corki.common.annotation.EnableScaApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableScaApplication
@MapperScan("com.corki.demo.dao.mapper")
public class CorkiScaOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorkiScaOrderApplication.class, args);
    }

}
