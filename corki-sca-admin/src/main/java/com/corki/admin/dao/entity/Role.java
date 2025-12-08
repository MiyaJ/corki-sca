package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("role")
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @TableField("role_key")
    private String roleKey;

    /**
     * 显示顺序
     */
    @TableField("role_sort")
    private Integer roleSort;

    /**
     * 数据范围：1-全部数据权限，2-自定义数据权限，3-本部门数据权限，4-本部门及以下数据权限
     */
    @TableField("data_scope")
    private Integer dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @TableField("menu_check_strictly")
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @TableField("dept_check_strictly")
    private Boolean deptCheckStrictly;

    /**
     * 角色状态：0-正常，1-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 用户是否存在此角色标识 默认不存在
     */
    @TableField(exist = false)
    private boolean flag = false;

    /**
     * 菜单组
     */
    @TableField(exist = false)
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @TableField(exist = false)
    private Long[] deptIds;

    /**
     * 角色菜单
     */
    @TableField(exist = false)
    private List<Menu> menus;
}