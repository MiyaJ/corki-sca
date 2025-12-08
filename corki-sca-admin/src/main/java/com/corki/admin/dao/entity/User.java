package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "`user`")
public class User {
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
     * 部门ID
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 删除标识; 0-未删除; 1-已删除
     */
    @TableField(value = "is_del")
    private Boolean isDel;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 角色组
     */
    @TableField(exist = false)
    private Long[] roleIds;

    /**
     * 角色列表
     */
    @TableField(exist = false)
    private List<Role> roles;
}