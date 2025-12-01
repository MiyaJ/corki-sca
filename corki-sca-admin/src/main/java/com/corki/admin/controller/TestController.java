package com.corki.admin.controller;

import com.corki.admin.feign.MemberFeignClient;
import com.corki.common.model.R;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Value("${test:777}")
    private String test;

    @Resource
    private MemberFeignClient memberFeignClient;

    @GetMapping("/hello")
    public String hello() {
        return "hello world: " + test;
    }

    @GetMapping("/feign/member/test")
    public R<String> test() {
        return memberFeignClient.test();
    }
}
