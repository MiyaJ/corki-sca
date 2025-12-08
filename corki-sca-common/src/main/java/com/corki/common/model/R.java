package com.corki.common.model;

import com.corki.common.enums.ResponseEnum;
import lombok.Data;

@Data
public class R<T>{

    private Integer code;
    private String message;
    private T data;

    public static <T> R<T> success(){
        R<T> r = new R<>();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMsg());
        return r;
    }

    public static <T> R<T> success(T data){
        R<T> r = new R<>();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> success(String message){
        R<T> r = new R<>();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(message);
        r.setData(null);
        return r;
    }

    public static <T> R<T> fail(Integer code, String message){
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(String message){
        R<T> r = new R<>();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(ResponseEnum responseEnum){
        R<T> r = new R<>();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMsg());
        return r;
    }

    public static <T> R<T> fail(){
        R<T> r = new R<>();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMsg());
        return r;
    }
}
