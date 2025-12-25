package com.corki.admin.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.corki.admin.common.enums.AdminUserStatusEnum;
import com.corki.admin.dao.entity.User;
import com.corki.admin.dao.service.UserService;
import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.admin.model.dto.LoginDTO;
import com.corki.admin.model.vo.LoginUserVO;
import com.corki.admin.model.vo.RouterVO;
import com.corki.common.enums.ResponseEnum;
import com.corki.common.model.R;
import com.corki.admin.utils.CaptchaService;
import com.corki.common.utils.StpAdminUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    @Override
    public R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req) {
        log.info("accountPwdLogin--->info: {}", JSONUtil.toJsonStr(req));
        User user = userService.lambdaQuery()
                .eq(User::getUsername, req.getUsername())
                .last("limit 1")
                .one();
        if (user == null) {
            return R.fail(ResponseEnum.USER_NOT_EXIST);
        }

        if (!AdminUserStatusEnum.NORMAL.is(user.getStatus())) {
            return R.fail(ResponseEnum.USER_STATUS_ERROR);
        }
        String md5Pwd = SaSecureUtil.md5(req.getPassword());
        if (!user.getPassword().equals(md5Pwd)) {
            return R.fail(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 更新登录信息, 登录日志
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginDevice(req.getDeviceType());
        userService.updateById(user);

        StpAdminUtil.login(user.getId(), String.valueOf(req.getDeviceType()));
        LoginUserRsp loginUserRsp = BeanUtil.copyProperties(user, LoginUserRsp.class);

        SaTokenInfo tokenInfo = StpAdminUtil.getTokenInfo();
        BeanUtil.copyProperties(tokenInfo, loginUserRsp);

        return R.success(loginUserRsp);
    }

    @Override
    public R<LoginUserVO> login(String username, String password) {
        return null;
    }

    @Override
    public R<LoginUserVO> login(LoginDTO loginDTO) {
        log.info("用户登录请求: {}", JSONUtil.toJsonStr(loginDTO));

        // 1. 校验验证码
        if (StrUtil.isEmpty(loginDTO.getUuid())) {
            return R.fail("验证码标识不能为空");
        }

        boolean captchaValid = captchaService.verifyCaptcha(loginDTO.getUuid(), loginDTO.getCode());
        if (!captchaValid) {
            return R.fail("验证码错误或已过期");
        }

        // 2. 查询用户
        User user = userService.lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername())
                .last("limit 1")
                .one();

        if (user == null) {
            return R.fail(ResponseEnum.USER_NOT_EXIST);
        }

        // 3. 检查用户状态
        if (!AdminUserStatusEnum.NORMAL.is(user.getStatus())) {
            return R.fail(ResponseEnum.USER_STATUS_ERROR);
        }

        // 4. 校验密码
        String md5Pwd = SaSecureUtil.md5(loginDTO.getPassword());
        if (!user.getPassword().equals(md5Pwd)) {
            return R.fail(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 5. 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);

        // 6. 执行登录
        StpAdminUtil.login(user.getId());

        // 7. 构建返回数据
        LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);

        // 设置token信息
        SaTokenInfo tokenInfo = StpAdminUtil.getTokenInfo();
        loginUserVO.setTokenName(tokenInfo.getTokenName());
        loginUserVO.setTokenValue(tokenInfo.getTokenValue());

        log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        // todo 保存登录日志

        return R.success(loginUserVO);
    }

    @Override
    public void logout() {
        // 获取当前登录用户
        Long userId = StpAdminUtil.getLoginIdAsLong();
        if (userId != null) {
            log.info("用户登出，用户ID: {}", userId);
        }

        // 执行登出
        StpAdminUtil.logout();
    }

    @Override
    public R<LoginUserVO> getInfo() {
        return null;
    }

    @Override
    public R<List<RouterVO>> getRouters() {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(SaSecureUtil.md5("123456"));
    }
}
