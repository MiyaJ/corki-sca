package com.corki.admin.service;

import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.admin.model.vo.LoginUserVO;
import com.corki.admin.model.vo.RouterVO;
import com.corki.common.model.R;

import java.util.List;

public interface ILoginService {

    R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req);

    R<LoginUserVO> login(String username, String password);

    void logout();

    R<LoginUserVO> getInfo();

    R<List<RouterVO>> getRouters();
}
