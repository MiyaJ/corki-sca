package com.corki.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.corki.admin.model.dto.LoginDTO;
import com.corki.admin.model.vo.LoginUserVO;
import com.corki.admin.model.vo.RouterVO;
import com.corki.admin.service.ILoginService;
import com.corki.common.model.R;
import com.corki.common.utils.StpAdminUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录控制器
 *
 * @author Corki
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/admin/auth")
public class LoginController {

    @Autowired
    private ILoginService loginService;



    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginUserVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO.getUsername(), loginDTO.getPassword());
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.success("退出成功");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getInfo")
    public R<LoginUserVO> getInfo() {
        return loginService.getInfo();
    }

    /**
     * 获取用户路由信息
     */
    @GetMapping("/getRouters")
    public R<List<RouterVO>> getRouters() {
        return loginService.getRouters();
    }

    /**
     * 获取用户权限信息
     */
    @GetMapping("/getPermissions")
    public R<List<String>> getPermissions() {
        try {
            Long userId = StpAdminUtil.getLoginIdAsLong();
            // TODO: 获取权限列表
            return R.success();
        } catch (Exception e) {
            return R.fail("获取权限信息失败");
        }
    }

}
