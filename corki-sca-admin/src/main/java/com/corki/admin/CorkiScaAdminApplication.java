package com.corki.admin;

import com.corki.common.annotation.EnableScaApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableScaApplication
@MapperScan("com.corki.admin.dao.mapper")
public class CorkiScaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorkiScaAdminApplication.class, args);
    }

}
