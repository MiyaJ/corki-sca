package com.corki.admin.service;

import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.common.model.R;

public interface ILoginService {

    R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req);
}
