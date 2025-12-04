package com.corki.member.controller;


import com.corki.common.model.R;
import com.corki.member.model.AccountPwdLoginReq;
import com.corki.member.model.LoginUserRsp;
import com.corki.member.service.ILoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 *
 * @author corkicai
 * @date 2025/12/04
 */
@RestController
@RequestMapping("/login")
@Tag(name = "登录接口")
public class LoginController {

    @Resource
    private ILoginService loginService;


    /**
     * 账密登录
     *
     * @param req 请求
     * @return {@link R }<{@link LoginUserRsp }>
     */
    @PostMapping("/accountPwdLogin")
    public R<LoginUserRsp> accountPwdLogin(@RequestBody AccountPwdLoginReq req) {
        return loginService.accountPwdLogin(req);
    }
}
