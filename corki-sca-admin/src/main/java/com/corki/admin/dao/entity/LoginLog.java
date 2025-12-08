package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统访问记录表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@TableName("login_log")
public class LoginLog {

    /**
     * 访问ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 登录IP地址
     */
    @TableField("ipaddr")
    private String ipaddr;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 登录状态（0-成功，1-失败）
     */
    @TableField("status")
    private Integer status;

    /**
     * 提示消息
     */
    @TableField("msg")
    private String msg;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;
}