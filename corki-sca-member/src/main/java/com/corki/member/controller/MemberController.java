package com.corki.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@Tag(name = "02-会员接口")
public class MemberController {

    @Operation(summary = "01-hello接口")
    @GetMapping("/hello")
    public String hello() {
        return "hello member";
    }
}
