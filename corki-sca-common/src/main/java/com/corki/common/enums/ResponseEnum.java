package com.corki.common.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    SUCCESS(200, "成功"),
    TOKEN_IS_INVALID(401, "token无效"),
    ERROR(500, "服务器错误"),

    USER_NOT_EXIST(10001, "用户不存在"),
    USER_PASSWORD_ERROR(10002, "用户密码错误"),
    USER_STATUS_ERROR(10003, "用户状态异常"),
    USER_NOT_LOGIN(10004, "用户未登录"),
    USER_LOGIN_ERROR(10005, "用户登录失败"),
    USER_LOGOUT_ERROR(10006, "用户登出失败"),
    USER_LOGOUT_SUCCESS(10007, "用户登出成功"),

    ;

    private Integer code;
    private String msg;

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
