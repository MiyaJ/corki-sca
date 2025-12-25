package com.corki.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaResult;
import com.corki.common.enums.ResponseEnum;
import com.corki.common.utils.StpAdminUtil;
import com.corki.common.utils.StpMemberUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关认证配置
 *
 * @author corki
 * @date 2025/12/01
 */
@Slf4j
@Configuration
public class SaTokenConfigure {

    @Resource
    private AppProperties appProperties;

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 开放地址（白名单）
                .setExcludeList(appProperties.getExcludePaths())
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 根据路径前缀进行不同的认证处理
                    // 管理后台认证 - 检查是否登录
                    SaRouter.match("/admin/**", r -> StpAdminUtil.checkLogin())
                            // 会员端认证 - 检查是否登录
                            .match("/member/**", r -> StpMemberUtil.checkLogin());

                    // 权限认证 - 可选，根据业务需求开启
                    // 例如：SaRouter.match("/admin/system/user/**", r -> StpAdminUtil.checkPermission("system:user:list"));
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // 记录异常日志
                    log.warn("网关认证异常: {}", e.getMessage());

                    // 根据异常类型返回不同的错误码
                    return switch (e) {
                        case NotLoginException notLoginException ->
                            // 未登录异常
                                SaResult.error(ResponseEnum.UN_AUTH.getMsg());
                        case NotRoleException notRoleException ->
                            // 角色不足异常
                                SaResult.error("角色权限不足");
                        case NotPermissionException notPermissionException ->
                            // 权限不足异常
                                SaResult.error("操作权限不足");
                        default ->
                            // 其他异常
                                SaResult.error(e.getMessage());
                    };
                });
    }
}
