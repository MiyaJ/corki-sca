package com.corki;

import com.corki.common.annotation.EnableScaApplication;
import org.springframework.boot.SpringApplication;

@EnableScaApplication
public class CorkiScaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorkiScaGatewayApplication.class, args);
    }

}
