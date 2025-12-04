package com.corki.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaResult;
import com.corki.common.utils.StpAdminUtil;
import com.corki.common.utils.StpMemberUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * sa-token配置
 *
 * @author corkicai
 * @date 2025/12/01
 */
@Slf4j
@Configuration
public class SaTokenConfigure {

    @Resource
    private AppProperties appProperties;

    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .setExcludeList(appProperties.getExcludePaths())
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    log.info("StpAdminUtil.isLogin()--->{}, StpMemberUtil.isLogin()--->{}", StpAdminUtil.isLogin(), StpMemberUtil.isLogin());
                    SaRouter.match(StpAdminUtil.isLogin() || StpMemberUtil.isLogin());
//                    SaRouter.match("/**", r -> StpAdminUtil.checkLogin());
//                    SaRouter.match("/**", r -> StpMemberUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
//                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));

                    // 更多匹配 ...  */
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                });
    }

}
