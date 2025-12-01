package com.corki.member.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountPwdLoginReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    /**
     * 设备类型: 1-PC; 2-APP; 3-小程序
     */
    private Integer deviceType;
}
