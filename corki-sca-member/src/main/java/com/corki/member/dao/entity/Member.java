package com.corki.member.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "`member`")
public class Member {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 手机号
     */
    @TableField(value = "mobile")
    private String mobile;

    /**
     * 状态: 1-正常; 2- 禁用; 3-注销
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 注册时间
     */
    @TableField(value = "registration_time")
    private LocalDateTime registrationTime;

    /**
     * 最后登录时间
     */
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录设备: 0-PC; 1-APP; 2-小程序
     */
    @TableField(value = "last_login_device")
    private Integer lastLoginDevice;

    /**
     * 删除标识; 0-未删除; 1-已删除
     */
    @TableField(value = "is_del")
    private Boolean isDel;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}