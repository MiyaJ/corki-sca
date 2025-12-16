package com.corki.admin.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.corki.admin.dao.entity.Role;
import com.corki.admin.dao.mapper.RoleMapper;
import com.corki.admin.dao.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色业务层处理
 *
 * @author Corki
 * @since 2024-12-09
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<Role> selectRoleList(Role role) {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        List<Role> userRoles = roleMapper.selectRoleListByUserId(userId);
        List<Role> roles = selectRoleAll();
        for (Role role : roles) {
            for (Role userRole : userRoles) {
                if (role.getId().longValue() == userRole.getId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<Role> selectRoleAll() {
        return this.lambdaQuery().list();
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return roleMapper.selectRoleListByUserIdChecked(userId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public Role selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(Role role) {
        Long roleId = role.getId() == null ? -1L : role.getId();
        Role info = roleMapper.checkRoleNameUnique(role.getRoleName());
        return info == null || info.getId().longValue() == roleId.longValue();
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(Role role) {
        Long roleId = role.getId() == null ? -1L : role.getId();
        Role info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        return info == null || info.getId().longValue() == roleId.longValue();
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(Role role) {
        if (role.getId() != null && role.getId() == 1L) {
            throw new RuntimeException("超级管理员角色不允许操作");
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleIds 角色id
     */
    @Override
    public void checkRoleDataScope(Long[] roleIds) {
        if (!isAdmin()) {
            // TODO: 实现数据权限校验逻辑
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        // TODO: 实现查询角色使用数量逻辑
        return 0;
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(Role role) {
        // 新增角色信息
        int rows = roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(Role role) {
        // 修改角色信息
        int rows = roleMapper.updateRole(role);
        // 删除角色与菜单关联
        // TODO: 实现删除角色菜单关联逻辑
        // 新增角色与菜单关联
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(Role role) {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(Role role) {
        // 修改角色信息
        int rows = roleMapper.updateRole(role);
        // 删除角色与部门关联
        // TODO: 实现删除角色部门关联逻辑
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        return roleMapper.deleteRoleById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new Role(roleId));
            Role role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RuntimeException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        return roleMapper.deleteRoleByIds(roleIds);
    }

    /**
     * 取消授权用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int deleteUserRoleInfo(Long userId, Long roleId) {
        return roleMapper.deleteUserRoleInfo(userId, roleId);
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteUserRoleInfos(Long roleId, Long[] userIds) {
        return roleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthRole(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        Arrays.stream(userIds).forEach(userId -> {
            checkRoleDataScope(new Long[]{roleId});
        });
        return roleMapper.batchInsertUserRole(roleId, userIds);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(Role role) {
        int rows = 1;
        // 新增用户与角色管理
        List<Long> list = Arrays.stream(role.getMenuIds()).toList();
        if (!list.isEmpty()) {
            // TODO: 实现批量插入角色菜单逻辑
            // rows = roleMenuMapper.batchRoleMenu(role.getId(), list);
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(Role role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<Long> list = Arrays.stream(role.getDeptIds()).toList();
        if (!list.isEmpty()) {
            // TODO: 实现批量插入角色部门逻辑
            // rows = roleDeptMapper.batchRoleDept(role.getId(), list);
        }
        return rows;
    }

    private boolean isAdmin() {
        // TODO: 实现管理员判断逻辑
        return false;
    }
}