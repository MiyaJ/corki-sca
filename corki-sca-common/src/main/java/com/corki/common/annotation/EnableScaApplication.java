package com.corki.common.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)  // 注解可以应用于类
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public @interface EnableScaApplication {
}
