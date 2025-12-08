package com.corki.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限认证配置类
 *
 * @author Corki
 * @since 2024-12-09
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器，校验规则为StpUtil.checkLogin()登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一条 match 规则
            SaRouter
                    .match("/**")    // 拦截的 path 列表，可以写多个
                    .notMatch("/admin/auth/login")    // 排除掉的 path 列表，可以写多个
                    .notMatch("/admin/auth/logout")   // 排除掉的 path 列表，可以写多个
                    .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式

            // 根据路由划分模块，不同模块不同鉴权
            SaRouter.match("/admin/system/user/**", r -> StpUtil.checkPermission("system:user"));
            SaRouter.match("/admin/system/role/**", r -> StpUtil.checkPermission("system:role"));
            SaRouter.match("/admin/system/menu/**", r -> StpUtil.checkPermission("system:menu"));

            // 更多路由匹配...
        })).addPathPatterns("/**");
    }
}