//package com.corki.admin.controller.system;
//
//import cn.dev33.satoken.annotation.SaCheckLogin;
//import cn.dev33.satoken.annotation.SaCheckPermission;
//import com.corki.admin.dao.entity.User;
//import com.corki.admin.dao.service.UserService;
//import com.corki.common.model.R;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 用户信息控制器
// *
// * @author Corki
// * @since 2024-12-09
// */
//@RestController
//@RequestMapping("/admin/system/user")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 获取用户分页列表
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:list")
//    @GetMapping("/list")
//    public R<IPage<User>> list(Page<User> page, User user) {
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.like(user.getUsername() != null, User::getUsername, user.getUsername())
//                .like(user.getEmail() != null, User::getEmail, user.getEmail())
//                .like(user.getMobile() != null, User::getMobile, user.getMobile())
//                .eq(user.getStatus() != null, User::getStatus, user.getStatus())
//                .eq(User::getIsDel, false)
//                .orderByDesc(User::getCreateTime);
//
//        IPage<User> result = userService.page(page, wrapper);
//        return R.success(result);
//    }
//
//    /**
//     * 根据用户编号获取详细信息
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:query")
//    @GetMapping(value = "/{userId}")
//    public R<User> getInfo(@PathVariable Long userId) {
//        User user = userService.getById(userId);
//        return R.success(user);
//    }
//
//    /**
//     * 新增用户
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:add")
//    @PostMapping
//    public R<Void> add(@Validated @RequestBody User user) {
//        // TODO: 检查用户名是否唯一
//        // TODO: 密码加密处理
//        boolean result = userService.save(user);
//        return result ? R.success("新增成功") : R.fail("新增失败");
//    }
//
//    /**
//     * 修改用户
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:edit")
//    @PutMapping
//    public R<Void> edit(@Validated @RequestBody User user) {
//        // TODO: 检查用户是否可以修改
//        boolean result = userService.updateById(user);
//        return result ? R.success("修改成功") : R.fail("修改失败");
//    }
//
//    /**
//     * 删除用户
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:remove")
//    @DeleteMapping("/{userIds}")
//    public R<Void> remove(@PathVariable Long[] userIds) {
//        List<Long> ids = Arrays.asList(userIds);
//        // TODO: 检查是否可以删除
//        boolean result = userService.removeByIds(ids);
//        return result ? R.success("删除成功") : R.fail("删除失败");
//    }
//
//    /**
//     * 重置密码
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:resetPwd")
//    @PutMapping("/resetPwd")
//    public R<Void> resetPwd(@RequestBody User user) {
//        // TODO: 密码加密处理
//        boolean result = userService.updateById(user);
//        return result ? R.success("重置成功") : R.fail("重置失败");
//    }
//
//    /**
//     * 状态修改
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:edit")
//    @PutMapping("/changeStatus")
//    public R<Void> changeStatus(@RequestBody User user) {
//        boolean result = userService.updateById(user);
//        return result ? R.success("修改成功") : R.fail("修改失败");
//    }
//
//    /**
//     * 根据用户编号获取授权角色
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:query")
//    @GetMapping("/authRole/{userId}")
//    public R<List<Long>> authRole(@PathVariable Long userId) {
//        // TODO: 获取用户角色信息
//        return R.success();
//    }
//
//    /**
//     * 用户授权角色
//     */
//    @SaCheckLogin
//    @SaCheckPermission("system:user:edit")
//    @PutMapping("/authRole")
//    public R<Void> insertAuthRole(@RequestBody User user) {
//        // TODO: 保存用户角色信息
//        return R.success("授权成功");
//    }
//}