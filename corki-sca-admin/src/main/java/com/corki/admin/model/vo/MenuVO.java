package com.corki.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单信息VO
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
public class MenuVO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否为外链：0-是，1-否
     */
    private Integer isFrame;

    /**
     * 是否缓存：0-缓存，1-不缓存
     */
    private Integer isCache;

    /**
     * 菜单类型：M-目录，C-菜单，F-按钮
     */
    private String menuType;

    /**
     * 菜单状态：0-显示，1-隐藏
     */
    private Integer visible;

    /**
     * 菜单状态：0-正常，1-停用
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子菜单
     */
    private List<MenuVO> children;
}