package com.van.monitor.util;

import java.io.Serializable;

/**
 * Created by van on 2016/11/23.
 */
public class RestResponse<T> implements Serializable{

    private int code;
    private String msg;
    private T data;

    public static <S> RestResponse getSuccResponse(S data){
        return new RestResponse<S>(200,"ok",data);
    }

    public RestResponse() {
    }

    public RestResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
