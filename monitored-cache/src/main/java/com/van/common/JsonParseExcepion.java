package com.van.common;

/**
 * Created by van on 2016/12/10.
 */
public class JsonParseExcepion extends PersistanseException{
    public JsonParseExcepion(String s) {
        super(s);
    }

    public JsonParseExcepion(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JsonParseExcepion(Throwable throwable) {
        super(throwable);
    }
}
