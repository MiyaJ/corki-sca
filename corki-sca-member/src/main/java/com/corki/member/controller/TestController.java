package com.corki.member.controller;

import com.corki.common.model.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RefreshScope
@Tag(name = "01-测试接口")
public class TestController {

    @Value("${test:777}")
    private String test;

    @GetMapping("/hello")
    public R<String> hello() {
        return R.success("hello world: " + test);
    }
}
