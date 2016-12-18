package com.van.common;

/**
 * Created by van on 2016/12/10.
 */
public class Assert {
    public static void asertNotNull(Object obj, String errorMsg){
        if(obj==null){
            throw new IllegalParamException(errorMsg);
        }
    }
    public static void asertNotNull(Object obj){
        asertNotNull(obj);
    }

    public static void asertEqual(Object obj, Object obj2, String errorMsg){
        asertNotNull(obj,errorMsg);
        if(!obj.equals(obj2)){
            throw new IllegalParamException(errorMsg);
        }
    }
    public static void asertEqual(Object obj, Object obj2){
        asertEqual(obj,obj2,"");
    }
}
