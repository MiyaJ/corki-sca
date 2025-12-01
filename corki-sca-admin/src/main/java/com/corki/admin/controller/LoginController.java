package com.corki.admin.controller;

import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.admin.service.ILoginService;
import com.corki.common.model.R;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private ILoginService loginService;

    @PostMapping("/accountPwdLogin")
    public R<LoginUserRsp> accountPwdLogin(@RequestBody AccountPwdLoginReq req) {
        return loginService.accountPwdLogin(req);
    }
}
