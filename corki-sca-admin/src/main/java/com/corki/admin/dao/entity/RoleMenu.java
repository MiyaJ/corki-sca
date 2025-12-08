package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@TableName("role_menu")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;

    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.INPUT)
    private Long menuId;
}