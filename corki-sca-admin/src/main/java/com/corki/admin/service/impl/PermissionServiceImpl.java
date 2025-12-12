//package com.corki.admin.service.impl;
//
//import cn.dev33.satoken.SaManager;
//import cn.dev33.satoken.secure.BCrypt;
//import cn.dev33.satoken.stp.StpUtil;
//import com.corki.admin.dao.entity.Menu;
//import com.corki.admin.dao.entity.Role;
//import com.corki.admin.dao.entity.User;
//import com.corki.admin.dao.service.IMenuService;
//import com.corki.admin.dao.service.IRoleService;
//import com.corki.admin.dao.service.UserService;
//import com.corki.admin.model.vo.LoginUserVO;
//import com.corki.admin.model.vo.RouterVO;
//import com.corki.admin.service.ILoginService;
//import com.corki.common.model.R;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 权限管理Service实现类
// *
// * @author Corki
// * @since 2024-12-09
// */
//@Service
//public class PermissionServiceImpl implements ILoginService {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private IRoleService roleService;
//
//    @Autowired
//    private IMenuService menuService;
//
//    /**
//     * 用户登录
//     *
//     * @param username 用户名
//     * @param password 密码
//     * @return 登录结果
//     */
//    @Override
//    public R<LoginUserVO> login(String username, String password) {
//        // 用户验证
//        User user = userService.lambdaQuery()
//                .eq(User::getUsername, username)
//                .eq(User::getStatus, 1) // 正常状态
//                .eq(User::getIsDel, false) // 未删除
//                .one();
//
//        if (user == null) {
//            return R.fail("用户不存在或已被禁用");
//        }
//
//        // 密码验证
//        if (!BCrypt.checkpw(password, user.getPassword())) {
//            return R.fail("密码错误");
//        }
//
//        // 生成token
//        StpUtil.login(user.getId());
//
//        // 获取用户权限信息
//        LoginUserVO loginUserVO = createLoginUser(user);
//
//        // 记录登录信息
//        recordLoginInfo(user.getId(), user.getUsername(), 1, "登录成功");
//
//        return R.success("登录成功", loginUserVO);
//    }
//
//    /**
//     * 用户登出
//     */
//    @Override
//    public void logout() {
//        try {
//            Long userId = StpUtil.getLoginIdAsLong();
//            recordLoginInfo(userId, "", 1, "退出成功");
//        } catch (Exception e) {
//            // 忽略异常
//        } finally {
//            StpUtil.logout();
//        }
//    }
//
//    /**
//     * 获取用户信息
//     *
//     * @return 用户信息
//     */
//    @Override
//    public R<LoginUserVO> getInfo() {
//        try {
//            Long userId = StpUtil.getLoginIdAsLong();
//
//            User user = userService.getById(userId);
//            if (user == null) {
//                return R.fail("用户不存在");
//            }
//
//            // 获取用户角色
//            List<Role> roles = roleService.selectRolesByUserId(userId);
//            List<String> roleKeys = roles.stream()
//                    .map(Role::getRoleKey)
//                    .collect(Collectors.toList());
//
//            // 获取用户权限
//            List<String> permissions = new ArrayList<>(menuService.selectMenuPermsByUserId(userId));
//
//            LoginUserVO loginUserVO = new LoginUserVO();
//            loginUserVO.setUserId(user.getId());
//            loginUserVO.setDeptId(user.getDeptId());
//            loginUserVO.setUsername(user.getUsername());
//            loginUserVO.setNickname(user.getNickname());
//            loginUserVO.setEmail(user.getEmail());
//            loginUserVO.setMobile(user.getMobile());
//            loginUserVO.setSex(user.getSex() == null ? "0" : user.getSex().toString());
//            loginUserVO.setAvatar(user.getAvatar());
//            loginUserVO.setRoles(roleKeys);
//            loginUserVO.setPermissions(permissions);
//
//            return R.success(loginUserVO);
//        } catch (Exception e) {
//            return R.fail("获取用户信息失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取用户路由信息
//     *
//     * @return 路由信息
//     */
//    @Override
//    public R<List<RouterVO>> getRouters() {
//        try {
//            Long userId = StpUtil.getLoginIdAsLong();
//            List<Menu> menus = menuService.selectMenuTreeByUserId(userId);
//            return R.success(buildMenus(menus));
//        } catch (Exception e) {
//            return R.fail("获取路由信息失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 创建登录用户信息
//     *
//     * @param user 用户信息
//     * @return 登录用户信息
//     */
//    private LoginUserVO createLoginUser(User user) {
//        LoginUserVO loginUserVO = new LoginUserVO();
//        BeanUtils.copyProperties(user, loginUserVO);
//
//        // 获取用户角色
//        List<Role> roles = roleService.selectRolesByUserId(user.getId());
//        List<String> roleKeys = roles.stream()
//                .map(Role::getRoleKey)
//                .collect(Collectors.toList());
//
//        // 获取用户权限
//        List<String> permissions = new ArrayList<>(menuService.selectMenuPermsByUserId(user.getId()));
//
//        loginUserVO.setRoles(roleKeys);
//        loginUserVO.setPermissions(permissions);
//
//        return loginUserVO;
//    }
//
//    /**
//     * 构建前端路由所需要的菜单
//     *
//     * @param menus 菜单列表
//     * @return 路由列表
//     */
//    private List<RouterVO> buildMenus(List<Menu> menus) {
//        List<RouterVO> routers = new ArrayList<>();
//        for (Menu menu : menus) {
//            RouterVO router = new RouterVO();
//            router.setName(menu.getMenuName());
//            router.setPath(menu.getPath());
//            router.setComponent(menu.getComponent());
//            router.setQuery(menu.getQuery());
//            router.setMeta(createMeta(menu));
//            List<Menu> children = menu.getChildren();
//            if (children != null && children.size() > 0 && "M".equals(menu.getMenuType())) {
//                // 目录类型，有子菜单
//                router.setRedirect("noRedirect");
//                router.setChildren(buildMenus(children));
//            }
//            routers.add(router);
//        }
//        return routers;
//    }
//
//    /**
//     * 创建路由元数据
//     *
//     * @param menu 菜单信息
//     * @return 路由元数据
//     */
//    private Map<String, Object> createMeta(Menu menu) {
//        Map<String, Object> meta = new HashMap<>();
//        meta.put("title", menu.getMenuName());
//        meta.put("icon", menu.getIcon());
//        meta.put("noCache", menu.getIsCache() == 1);
//        if (menu.getIsFrame() == 0) {
//            meta.put("link", menu.getPath());
//        }
//        return meta;
//    }
//
//    /**
//     * 记录登录信息
//     *
//     * @param userId 用户ID
//     * @param username 用户名
//     * @param status 状态
//     * @param message 消息
//     */
//    private void recordLoginInfo(Long userId, String username, int status, String message) {
//        // TODO: 实现登录日志记录
//        // 可以异步记录到数据库
//    }
//
//    /**
//     * 检查用户权限
//     *
//     * @param permission 权限标识
//     * @return 是否有权限
//     */
//    public boolean checkPermission(String permission) {
//        try {
//            Long userId = StpUtil.getLoginIdAsLong();
//            if (userId == null || userId == 1L) {
//                // 超级管理员直接放行
//                return true;
//            }
//
//            // 检查用户是否拥有该权限
//            List<String> permissions = menuService.selectMenuPermsByUserId(userId);
//            return permissions.contains("*:*:*") || permissions.contains(permission);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 检查用户角色
//     *
//     * @param role 角色标识
//     * @return 是否有角色
//     */
//    public boolean checkRole(String role) {
//        try {
//            Long userId = StpUtil.getLoginIdAsLong();
//            if (userId == null || userId == 1L) {
//                return true;
//            }
//
//            List<Role> roles = roleService.selectRolesByUserId(userId);
//            return roles.stream()
//                    .anyMatch(r -> role.equals(r.getRoleKey()));
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}