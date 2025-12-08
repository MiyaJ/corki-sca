package com.corki.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.corki.admin.dao.entity.Role;
import com.corki.admin.dao.service.IRoleService;
import com.corki.common.core.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色信息控制器
 *
 * @author Corki
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/admin/system/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    /**
     * 获取角色列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public R<List<Role>> list(Role role) {
        List<Role> list = roleService.selectRoleList(role);
        return R.ok(list);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R<Role> getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(new Long[]{roleId});
        Role role = roleService.selectRoleById(roleId);
        return R.ok(role);
    }

    /**
     * 新增角色
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody Role role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUsername());
        return roleService.insertRole(role) > 0 ? R.ok("新增成功") : R.fail("新增失败");
    }

    /**
     * 修改保存角色
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody Role role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(new Long[]{role.getId()});

        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        role.setUpdateBy(getUsername());

        if (roleService.updateRole(role) > 0) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody Role role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(new Long[]{role.getId()});
        return roleService.authDataScope(role) > 0 ? R.ok("修改成功") : R.fail("修改失败");
    }

    /**
     * 状态修改
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody Role role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(getUsername());
        return roleService.updateRoleStatus(role) > 0 ? R.ok("修改成功") : R.fail("修改失败");
    }

    /**
     * 删除角色
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:remove")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        return roleService.deleteRoleByIds(roleIds) > 0 ? R.ok("删除成功") : R.fail("删除失败");
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    public R<List<Role>> optionselect() {
        return R.ok(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public R<List<Role>> allocatedList(Role role) {
        List<Role> list = roleService.selectRoleList(role);
        return R.ok(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public R<List<Role>> unallocatedList(Role role) {
        List<Role> list = roleService.selectRoleList(role);
        return R.ok(list);
    }

    /**
     * 取消授权用户
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody Role role) {
        return roleService.deleteUserRoleInfo(role.getUserId(), role.getId()) > 0 ?
               R.ok("取消授权成功") : R.fail("取消授权失败");
    }

    /**
     * 批量取消授权用户
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Long roleId, Long[] userIds) {
        return roleService.deleteUserRoleInfos(roleId, userIds) > 0 ?
               R.ok("取消授权成功") : R.fail("取消授权失败");
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckLogin
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(new Long[]{roleId});
        return roleService.insertAuthRole(roleId, userIds) > 0 ?
               R.ok("授权成功") : R.fail("授权失败");
    }

    /**
     * 获取当前用户名
     */
    private String getUsername() {
        // TODO: 从Sa-Token获取当前用户名
        return "admin";
    }
}