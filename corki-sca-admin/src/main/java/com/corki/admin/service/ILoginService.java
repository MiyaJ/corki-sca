package com.corki.admin.service;

import com.corki.admin.model.AccountPwdLoginReq;
import com.corki.admin.model.LoginUserRsp;
import com.corki.admin.model.dto.LoginDTO;
import com.corki.admin.model.vo.LoginUserVO;
import com.corki.admin.model.vo.RouterVO;
import com.corki.common.model.R;

import java.util.List;

public interface ILoginService {

    R<LoginUserRsp> accountPwdLogin(AccountPwdLoginReq req);

    R<LoginUserVO> login(String username, String password);

    /**
     * 用户登录（带验证码）
     *
     * @param loginDTO 登录请求DTO
     * @return 登录结果
     */
    R<LoginUserVO> login(LoginDTO loginDTO);

    void logout();

    R<LoginUserVO> getInfo();

    R<List<RouterVO>> getRouters();
}
