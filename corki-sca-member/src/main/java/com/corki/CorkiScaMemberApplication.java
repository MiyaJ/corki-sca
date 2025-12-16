package com.corki;

import com.corki.common.annotation.EnableScaApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableScaApplication
@MapperScan("com.corki.member.dao.mapper")
public class CorkiScaMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorkiScaMemberApplication.class, args);
    }

}
