package com.corki.admin.common.enums;

import lombok.Getter;

@Getter
public enum DeviceTypeEnum {

    PC(1, "PC"),
    APP(2, "APP"),
    MINI_PROGRAM(3, "小程序"),
    ;

    private Integer code;
    private String msg;

    DeviceTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
