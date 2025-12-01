package com.corki.member.common.enums;

import lombok.Getter;

@Getter
public enum AdminUserStatusEnum {

    NORMAL(1, "正常"),
    DISABLED(2, "禁用"),
    LOGOUT(3, "注销"),
    ;

    private Integer code;
    private String msg;

    AdminUserStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean is(Integer code) {
        return this.getCode().equals(code);
    }

}
