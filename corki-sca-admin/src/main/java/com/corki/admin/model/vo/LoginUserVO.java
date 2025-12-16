package com.corki.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录用户信息VO
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
public class LoginUserVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    private String tokenName;
    private String tokenValue;
    private String loginDevice;
}