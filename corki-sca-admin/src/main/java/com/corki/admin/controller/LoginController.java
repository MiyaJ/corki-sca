package com.corki.admin.controller;

import cn.hutool.core.io.IoUtil;
import com.corki.admin.model.dto.LoginDTO;
import com.corki.admin.model.vo.LoginUserVO;
import com.corki.admin.model.vo.RouterVO;
import com.corki.admin.service.ILoginService;
import com.corki.common.model.R;
import com.corki.admin.utils.CaptchaService;
import com.corki.common.utils.StpAdminUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author Corki
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ILoginService loginService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码图片
     */
    @GetMapping("/captchaImage")
    public R<Map<String, Object>> getCaptchaImage(@RequestParam String uuid) {
        CaptchaService.CaptchaResponse captchaResponse = captchaService.generateCaptcha(uuid);

        Map<String, Object> result = new HashMap<>();
        result.put("uuid", captchaResponse.getUuid());
        result.put("img", captchaResponse.getImage());
        result.put("expireTime", captchaResponse.getExpireTime());

        return R.success(result);
    }

    /**
     * 获取验证码图片流
     */
    @GetMapping("/captcha/image")
    public R<Map<String, Object>> getCaptchaImageStream(@RequestParam String uuid, HttpServletResponse response) throws IOException {
        // 生成验证码
        CaptchaService.CaptchaResponse captchaResponse = captchaService.generateCaptcha(uuid);
        BufferedImage image = captchaResponse.getImage();

        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 输出图片
        javax.imageio.ImageIO.write(image, "png", response.getOutputStream());
        IoUtil.close(response.getOutputStream());

        Map<String, Object> result = new HashMap<>();
        result.put("uuid", captchaResponse.getUuid());
        result.put("img", captchaResponse.getImage());
        result.put("expireTime", captchaResponse.getExpireTime());

        return R.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginUserVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
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
