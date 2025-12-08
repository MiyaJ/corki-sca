package com.corki.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.corki.admin.dao.service.IMenuService;
import com.corki.admin.dao.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限认证接口实现类
 *
 * @author Corki
 * @since 2024-12-09
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRoleService roleService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的权限列表
        Long userId = Long.valueOf(loginId.toString());
        return (List<String>) menuService.selectMenuPermsByUserId(userId);
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的角色列表
        Long userId = Long.valueOf(loginId.toString());
        List<com.corki.admin.dao.entity.Role> roles = roleService.selectRolesByUserId(userId);
        return roles.stream()
                .map(com.corki.admin.dao.entity.Role::getRoleKey)
                .collect(java.util.stream.Collectors.toList());
    }
}