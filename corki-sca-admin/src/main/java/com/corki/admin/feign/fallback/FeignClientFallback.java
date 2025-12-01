package com.corki.admin.feign.fallback;

import com.corki.admin.feign.MemberFeignClient;
import com.corki.common.model.R;

public class FeignClientFallback implements MemberFeignClient {

    @Override
    public R<String> test() {
        return null;
    }
}
