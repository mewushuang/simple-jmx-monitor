package com.van.common;

import java.util.List;

/**
 * Created by van on 2016/12/10.
 */
public class ScodeEntity {
    public static final Integer SEAT_TYPE=11;
    public static final Integer RT_TYPE=12;


    private String scode;
    private String time;
    private int type;
    private List data;

    public ScodeEntity(String scode, String time, List data) {
        this.scode = scode;
        this.time = time;
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public ScodeEntity() {

    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String t="empty";
        if(getType()==SEAT_TYPE) t="seat";
        if(getType()==RT_TYPE) t="rt";
        return "{type:"+t+", scode:"+getScode()+", time:"+getTime()+", data:"+getData().toString()+"}";
    }
}
