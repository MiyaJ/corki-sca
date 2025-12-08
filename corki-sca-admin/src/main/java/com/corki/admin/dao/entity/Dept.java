package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dept")
public class Dept {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父部门ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 祖级列表
     */
    @TableField("ancestors")
    private String ancestors;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 显示顺序
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 负责人
     */
    @TableField("leader")
    private String leader;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 部门状态：0-正常，1-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建者
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<Dept> children = new ArrayList<>();

    /**
     * 父部门名称
     */
    @TableField(exist = false)
    private String parentName;
}