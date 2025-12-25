package com.corki.common.utils;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.corki.common.enums.LoginUserTypeEnum;

/**
 * StpLogic 门面类，管理项目中所有的 StpLogic 账号体系
 *
 * @author Corki
 * @date 2025/12/25
 */
public class StpKit {

    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Admin 会话对象，管理 Admin 表所有账号的登录、权限认证
     */
    public static final StpLogic ADMIN = new StpLogic(LoginUserTypeEnum.ADMIN.getUserType());

    /**
     * member 会话对象，管理 member 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogic(LoginUserTypeEnum.MEMBER.getUserType());
}
