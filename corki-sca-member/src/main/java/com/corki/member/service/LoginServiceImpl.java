package com.corki.member.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.bean.BeanUtil;
import com.corki.common.enums.ResponseEnum;
import com.corki.common.model.R;
import com.corki.common.utils.StpMemberUtil;
import com.corki.member.dao.entity.Member;
import com.corki.member.dao.service.MemberService;
import com.corki.member.model.AccountPwdLoginReq;
import com.corki.member.model.LoginUserRsp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    private MemberService memberService;
    /**
     * 账号密码登录
     *
     * @param req 请求
     * @return {@link R }<{@link LoginUserRsp }>
     */
    @Override
    public R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req) {
        String username = req.getUsername();
        String password = req.getPassword();
        Member member = memberService.lambdaQuery()
                .eq(Member::getUsername, username)
                .last("limit 1")
                .one();
        if (member == null) {
            return R.fail(ResponseEnum.USER_NOT_EXIST);
        }

        if (!SaSecureUtil.md5(password).equals(member.getPassword())) {
            return R.fail(ResponseEnum.USER_PASSWORD_ERROR);
        }
        // 更新登录信息, 登录日志
        member.setLastLoginTime(LocalDateTime.now());
        member.setLastLoginDevice(req.getDeviceType());
        memberService.updateById(member);

        StpMemberUtil.login(member.getId(), String.valueOf(req.getDeviceType()));
        LoginUserRsp loginUserRsp = BeanUtil.copyProperties(member, LoginUserRsp.class);

        SaTokenInfo tokenInfo = StpMemberUtil.getTokenInfo();
        BeanUtil.copyProperties(tokenInfo, loginUserRsp);

        return R.success(loginUserRsp);
    }
}
