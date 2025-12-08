package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色部门关联表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@TableName("role_dept")
public class RoleDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;

    /**
     * 部门ID
     */
    @TableId(value = "dept_id", type = IdType.INPUT)
    private Long deptId;
}