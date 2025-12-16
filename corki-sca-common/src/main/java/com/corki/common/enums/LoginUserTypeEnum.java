package com.corki.common.enums;

import lombok.Getter;

@Getter
public enum LoginUserTypeEnum {

    ADMIN("admin", "系统后台"),
    MEMBER("member", "会员"),
    ;

    private final String userType;
    private final String msg;

    LoginUserTypeEnum(String userType, String msg) {
        this.userType = userType;
        this.msg = msg;
    }
}
