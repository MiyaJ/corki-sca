package com.corki.admin.feign;

import com.corki.admin.feign.fallback.FeignClientFallback;
import com.corki.common.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "corki-sca-member", fallback = FeignClientFallback.class)
public interface MemberFeignClient {

    @GetMapping("/feign/api/test")
    R<String> test();
}
