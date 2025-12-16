package com.corki.admin.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountPwdLoginReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 设备类型: 1-PC; 2-APP; 3-小程序
     */
    private Integer deviceType;
}
