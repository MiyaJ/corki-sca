package com.corki.admin.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * 权限工具类
 *
 * @author Corki
 * @since 2024-12-09
 */
public class PermissionUtils {

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermission(String permission) {
        return StpUtil.hasPermission(permission);
    }

    /**
     * 判断当前用户是否含有所有指定权限
     *
     * @param permissions 以逗号分隔的权限列表
     * @return 用户是否具备所有权限
     */
    public static boolean hasAllPermissions(String permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        String[] permissionArray = permissions.split(",");
        for (String permission : permissionArray) {
            if (!StpUtil.hasPermission(permission.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前用户是否含有任意一个指定权限
     *
     * @param permissions 以逗号分隔的权限列表
     * @return 用户是否具备某一权限
     */
    public static boolean hasAnyPermissions(String permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        String[] permissionArray = permissions.split(",");
        for (String permission : permissionArray) {
            if (StpUtil.hasPermission(permission.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否拥有指定角色
     *
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role) {
        return StpUtil.hasRole(role);
    }

    /**
     * 判断当前用户是否含有所有指定角色
     *
     * @param roles 以逗号分隔的角色列表
     * @return 用户是否具备所有角色
     */
    public static boolean hasAllRoles(String roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        String[] roleArray = roles.split(",");
        for (String role : roleArray) {
            if (!StpUtil.hasRole(role.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前用户是否含有任意一个指定角色
     *
     * @param roles 以逗号分隔的角色列表
     * @return 用户是否具备某一角色
     */
    public static boolean hasAnyRoles(String roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        String[] roleArray = roles.split(",");
        for (String role : roleArray) {
            if (StpUtil.hasRole(role.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        // TODO: 从缓存或数据库获取用户名
        return StpUtil.getLoginId().toString();
    }

    /**
     * 判断当前用户是否是管理员
     *
     * @return 是否是管理员
     */
    public static boolean isAdmin() {
        Long userId = getUserId();
        return userId != null && userId == 1L;
    }

    /**
     * 密码加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为匿名用户
     *
     * @return 是否为匿名用户
     */
    public static boolean isAnonymous() {
        return !StpUtil.isLogin();
    }

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限列表
     */
    public static List<String> getPermissionList() {
        return StpUtil.getPermissionList();
    }

    /**
     * 获取当前用户的角色列表
     *
     * @return 角色列表
     */
    public static List<String> getRoleList() {
        return StpUtil.getRoleList();
    }

    /**
     * 生成权限标识
     *
     * @param module 模块名称
     * @param function 功能名称
     * @param operation 操作类型
     * @return 权限标识
     */
    public static String generatePermission(String module, String function, String operation) {
        return String.format("%s:%s:%s", module, function, operation);
    }
}