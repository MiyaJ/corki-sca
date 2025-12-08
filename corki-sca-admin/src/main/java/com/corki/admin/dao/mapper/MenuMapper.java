package com.corki.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.corki.admin.dao.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 数据层
 *
 * @author Corki
 * @since 2024-12-09
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    Set<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<Menu> selectMenuList(Menu menu);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectMenuListByUserId(Menu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<Menu> buildMenuTreeSelect(List<Menu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    Menu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(Menu menu);

    /**
     * 修改菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(Menu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    Menu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}