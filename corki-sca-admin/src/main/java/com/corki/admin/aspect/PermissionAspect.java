package com.corki.admin.aspect;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.corki.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限校验 AOP
 *
 * @author Corki
 * @since 2024-12-09
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    /**
     * 定义AOP签名 (切入所有使用SaCheckPermission注解的方法)
     */
    @Pointcut("@annotation(cn.dev33.satoken.annotation.SaCheckPermission)")
    public void checkPermission() {
    }

    /**
     * 定义AOP签名 (切入所有使用SaCheckRole注解的方法)
     */
    @Pointcut("@annotation(cn.dev33.satoken.annotation.SaCheckRole)")
    public void checkRole() {
    }

    /**
     * 环绕增强
     */
    @Before("checkPermission() || checkRole()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 检查权限注解
        SaCheckPermission checkPermission = method.getAnnotation(SaCheckPermission.class);
        if (checkPermission != null) {
            String[] value = checkPermission.value();
            if (!StpUtil.hasPermissionOr(value)) {
                log.warn("用户 {} 没有权限 {}", StpUtil.getLoginIdDefaultNull(), value);
                throw new ServiceException("没有访问权限，请联系管理员授权");
            }
        }

        // 检查角色注解
        SaCheckRole checkRole = method.getAnnotation(SaCheckRole.class);
        if (checkRole != null) {
            String[] value = checkRole.value();
            if (!StpUtil.hasRoleOr(value)) {
                log.warn("用户 {} 没有角色 {}", StpUtil.getLoginIdDefaultNull(), value);
                throw new ServiceException("没有访问权限，请联系管理员授权");
            }
        }
    }
}