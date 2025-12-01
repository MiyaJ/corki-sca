package com.corki.member.controller.feign;

import com.corki.common.model.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/api")
public class MemberFeignApi {

    @GetMapping("/test")
    public R<String> test() {
        return R.success("member feign test");
    }
}
