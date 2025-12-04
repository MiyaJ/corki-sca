package com.corki.member.service;

import com.corki.common.model.R;
import com.corki.member.model.AccountPwdLoginReq;
import com.corki.member.model.LoginUserRsp;

public interface ILoginService {
    /**
     * 账号密码登录
     *
     * @param req 请求
     * @return {@link R }<{@link LoginUserRsp }>
     */
    R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req);
}
