package com.corki.admin.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.corki.admin.common.enums.AdminUserStatusEnum;
import com.corki.admin.dao.entity.User;
import com.corki.admin.dao.service.UserService;
import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.common.enums.ResponseEnum;
import com.corki.common.model.R;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    private UserService userService;

    @Override
    public R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req) {
        log.info("accountPwdLogin--->info: {}", JSONUtil.toJsonStr(req));
        User user = userService.lambdaQuery()
                .eq(User::getUsername, req.getUsername())
                .last("limit 1")
                .one();
        if (user == null) {
            return R.error(ResponseEnum.USER_NOT_EXIST);
        }

        if (!AdminUserStatusEnum.NORMAL.is(user.getStatus())) {
            return R.error(ResponseEnum.USER_STATUS_ERROR);
        }
        String md5Pwd = SaSecureUtil.md5(req.getPassword());
        if (!user.getPassword().equals(md5Pwd)) {
            return R.error(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 更新登录信息, 登录日志
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginDevice(req.getDeviceType());
        userService.updateById(user);

        StpUtil.login(user.getId(), String.valueOf(req.getDeviceType()));
        LoginUserRsp loginUserRsp = BeanUtil.copyProperties(user, LoginUserRsp.class);

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BeanUtil.copyProperties(tokenInfo, loginUserRsp);

        return R.success(loginUserRsp);
    }

}
