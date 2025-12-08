package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单权限表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("menu")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 路由参数
     */
    @TableField("query")
    private String query;

    /**
     * 是否为外链：0-是，1-否
     */
    @TableField("is_frame")
    private Integer isFrame;

    /**
     * 是否缓存：0-缓存，1-不缓存
     */
    @TableField("is_cache")
    private Integer isCache;

    /**
     * 菜单类型：M-目录，C-菜单，F-按钮
     */
    @TableField("menu_type")
    private String menuType;

    /**
     * 菜单状态：0-显示，1-隐藏
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 菜单状态：0-正常，1-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;

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
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 参数
     */
    @TableField(exist = false)
    private Map<String, Object> params;
}