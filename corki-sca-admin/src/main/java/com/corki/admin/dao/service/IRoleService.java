package com.corki.admin.dao.service;

import com.corki.admin.dao.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author Corki
 * @since 2024-12-09
 */
public interface IRoleService extends IService<Role> {

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    List<Role> selectRoleList(Role role);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    List<Long> selectRoleListByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    Role selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleNameUnique(Role role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleKeyUnique(Role role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(Role role);

    /**
     * 校验角色是否有数据权限
     *
     * @param roleIds 角色id
     */
    void checkRoleDataScope(Long[] roleIds);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(Role role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(Role role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRoleStatus(Role role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int authDataScope(Role role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 取消授权用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteUserRoleInfo(Long userId, Long roleId);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    int deleteUserRoleInfos(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    int insertAuthRole(Long roleId, Long[] userIds);
}