package com.corki.admin.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MqDTO implements Serializable {

    private String orderNo;
    private String name;
    private String title;
    private String description;
}
