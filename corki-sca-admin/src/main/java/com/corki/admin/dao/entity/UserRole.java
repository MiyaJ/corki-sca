package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@TableName("user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;
}