package com.corki.common.config;

import cn.dev33.satoken.same.SaSameUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * feign拦截器, 在feign请求发出之前，加入一些操作
 *
 * @author Corki
 * @date 2025/12/25
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    /**
     * 为 Feign 的 RPC 调用 添加请求头Same-Token
     * 在调用的 FeignClient中引入该拦截器配置 configuration = FeignInterceptor.class
     *
     * @param requestTemplate 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
    }
}
