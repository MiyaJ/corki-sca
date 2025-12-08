package com.corki.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 路由信息VO
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
public class RouterVO {

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由：0-否，1-是
     */
    private Integer hidden;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数：如 { "id": 1, "name": "ry" }
     */
    private String query;

    /**
     * 路由别名：如 a-b-c
     */
    private String routeName;

    /**
     * 是否为外链：0-否，1-是
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
     * 菜单图标
     */
    private String icon;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 子路由
     */
    private List<RouterVO> children;
}