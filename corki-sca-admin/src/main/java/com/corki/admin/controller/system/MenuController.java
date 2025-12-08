package com.corki.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.corki.admin.dao.entity.Menu;
import com.corki.admin.dao.service.IMenuService;
import com.corki.common.core.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息控制器
 *
 * @author Corki
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/admin/system/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    /**
     * 获取菜单列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<Menu>> list(Menu menu) {
        Long userId = getUserId();
        List<Menu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<Menu> getInfo(@PathVariable("menuId") Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:query")
    @GetMapping("/treeselect")
    public R<List<Menu>> treeselect(Menu menu) {
        Long userId = getUserId();
        List<Menu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<Object> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        Long userId = getUserId();
        List<Menu> menus = menuService.selectMenuList(new Menu(), userId);
        return R.ok()
                .data("checkedKeys", menuService.selectMenuListByRoleId(roleId))
                .data("menus", menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 新增菜单
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody Menu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        menu.setCreateBy(getUsername());
        return menuService.insertMenu(menu) > 0 ? R.ok("新增成功") : R.fail("新增失败");
    }

    /**
     * 修改菜单
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody Menu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (menu.getId().equals(menu.getParentId())) {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUsername());
        return menuService.updateMenu(menu) > 0 ? R.ok("修改成功") : R.fail("修改失败");
    }

    /**
     * 删除菜单
     */
    @SaCheckLogin
    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.fail("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.fail("菜单已分配,不允许删除");
        }
        return menuService.deleteMenuById(menuId) > 0 ? R.ok("删除成功") : R.fail("删除失败");
    }

    /**
     * 获取当前用户ID
     */
    private Long getUserId() {
        // TODO: 从Sa-Token获取当前用户ID
        return 1L;
    }

    /**
     * 获取当前用户名
     */
    private String getUsername() {
        // TODO: 从Sa-Token获取当前用户名
        return "admin";
    }
}